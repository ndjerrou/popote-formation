package fr.formation.popote.domain

import kotlinx.coroutines.flow.Flow

// region repositories
/**
 * Contrats de la couche Data, vus depuis le Domain.
 * L'UI ne sait pas SI les données viennent de la mémoire, de Room ou du réseau.
 */
interface RecipeRepository {
    suspend fun search(query: String): List<RecipeSummary>
    suspend fun getRecipe(id: String): Recipe
    fun observeFavorites(): Flow<List<RecipeSummary>>
    suspend fun toggleFavorite(recipe: RecipeSummary)
}

interface ShoppingRepository {
    fun observeItems(): Flow<List<ShoppingItem>>
    suspend fun addRecipe(recipe: Recipe)
    suspend fun toggle(item: ShoppingItem)
    suspend fun clearChecked()
}
// endregion repositories

fun Recipe.toSummary() = RecipeSummary(id, name, thumbnailUrl)
