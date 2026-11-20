package fr.formation.popote.data.fake

import fr.formation.popote.data.FakeRecipes
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.RecipeRepository
import fr.formation.popote.domain.RecipeSummary
import fr.formation.popote.domain.ShoppingItem
import fr.formation.popote.domain.ShoppingListBuilder
import fr.formation.popote.domain.ShoppingRepository
import fr.formation.popote.domain.toSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// region inmemory
/** J3 : implémentation en mémoire, avec une latence simulée pour "sentir" l'asynchrone. */
class InMemoryRecipeRepository(private val latencyMs: Long = 800) : RecipeRepository {

    private val favorites = MutableStateFlow<List<RecipeSummary>>(emptyList())

    override suspend fun search(query: String): List<RecipeSummary> {
        delay(latencyMs)
        return FakeRecipes.all
            .filter { it.name.contains(query, ignoreCase = true) }
            .map { it.toSummary() }
    }

    override suspend fun getRecipe(id: String): Recipe {
        delay(latencyMs)
        return FakeRecipes.byId(id) ?: throw NoSuchElementException("Recette $id introuvable")
    }

    override fun observeFavorites(): Flow<List<RecipeSummary>> = favorites.asStateFlow()

    override suspend fun toggleFavorite(recipe: RecipeSummary) = favorites.update { current ->
        if (current.any { it.id == recipe.id }) current.filterNot { it.id == recipe.id } else current + recipe
    }
}

class InMemoryShoppingRepository : ShoppingRepository {
    private val items = MutableStateFlow<List<ShoppingItem>>(emptyList())

    override fun observeItems(): Flow<List<ShoppingItem>> = items.asStateFlow()

    override suspend fun addRecipe(recipe: Recipe) = items.update { current ->
        ShoppingListBuilder.merge(current, ShoppingListBuilder.build(listOf(recipe)))
    }

    override suspend fun toggle(item: ShoppingItem) = items.update { current ->
        current.map { if (it == item) it.copy(checked = !it.checked) else it }
    }

    override suspend fun clearChecked() = items.update { current -> current.filterNot { it.checked } }
}
// endregion inmemory
