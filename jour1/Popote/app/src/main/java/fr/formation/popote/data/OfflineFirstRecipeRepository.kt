package fr.formation.popote.data

import fr.formation.popote.data.local.FavoriteDao
import fr.formation.popote.data.local.FavoriteEntity
import fr.formation.popote.data.local.RecipeCacheDao
import fr.formation.popote.data.local.RecipeCacheEntity
import fr.formation.popote.data.remote.MealDbApi
import fr.formation.popote.data.remote.MealMapper
import fr.formation.popote.data.remote.safeApiCall
import fr.formation.popote.domain.PopoteException
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.RecipeRepository
import fr.formation.popote.domain.RecipeSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

// region offlinefirst
/** J4 : même interface qu'en J3, mais réseau (Retrofit) + base locale (Room). */
class OfflineFirstRecipeRepository(
    private val api: MealDbApi,
    private val mapper: MealMapper,
    private val favoriteDao: FavoriteDao,
    private val cacheDao: RecipeCacheDao,
    private val json: Json,
) : RecipeRepository {

    override suspend fun search(query: String): List<RecipeSummary> = safeApiCall {
        api.search(query).meals.orEmpty().map(mapper::toSummary)
    }

    override suspend fun getRecipe(id: String): Recipe {
        return try {
            val meal = safeApiCall { api.lookup(id).meals?.firstOrNull() }
                ?: throw PopoteException.NotFound("Recette $id")
            cacheDao.upsert(RecipeCacheEntity(id, meal.toString()))   // on met en cache
            mapper.toRecipe(meal)
        } catch (e: PopoteException.Network) {
            // Hors-ligne : on se rabat sur le cache s'il existe
            val cached = cacheDao.get(id) ?: throw e
            mapper.toRecipe(json.decodeFromString(JsonObject.serializer(), cached.json))
        }
    }

    override fun observeFavorites(): Flow<List<RecipeSummary>> =
        favoriteDao.observeAll().map { list -> list.map { RecipeSummary(it.id, it.name, it.thumbnailUrl) } }

    override suspend fun toggleFavorite(recipe: RecipeSummary) {
        if (favoriteDao.exists(recipe.id)) favoriteDao.delete(recipe.id)
        else favoriteDao.insert(FavoriteEntity(recipe.id, recipe.name, recipe.thumbnailUrl))
    }
}
// endregion offlinefirst
