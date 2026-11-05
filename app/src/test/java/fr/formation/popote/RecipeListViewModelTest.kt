package fr.formation.popote

import fr.formation.popote.domain.PopoteException
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.RecipeRepository
import fr.formation.popote.domain.RecipeSummary
import fr.formation.popote.ui.list.RecipeListUiState
import fr.formation.popote.ui.list.RecipeListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// region faketest
/** Un "fake" écrit à la main : souvent plus lisible qu'un mock pour les flux. */
private class FakeRecipeRepository : RecipeRepository {
    val queries = mutableListOf<String>()
    var failure: Exception? = null
    private val favorites = MutableStateFlow<List<RecipeSummary>>(emptyList())

    override suspend fun search(query: String): List<RecipeSummary> {
        queries += query
        failure?.let { throw it }
        return listOf(RecipeSummary("1", "Result for $query", null))
    }
    override suspend fun getRecipe(id: String): Recipe = error("unused")
    override fun observeFavorites(): Flow<List<RecipeSummary>> = favorites
    override suspend fun toggleFavorite(recipe: RecipeSummary) { favorites.value = favorites.value + recipe }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    @Test
    fun `typing quickly triggers only one search thanks to debounce`() = runTest(mainDispatcherRule.dispatcher) {
        val repo = FakeRecipeRepository()
        val viewModel = RecipeListViewModel(repo)
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onQueryChange("c"); advanceTimeBy(100)
        viewModel.onQueryChange("ch"); advanceTimeBy(100)
        viewModel.onQueryChange("chicken"); advanceTimeBy(301); runCurrent()

        assertEquals(listOf("chicken"), repo.queries)   // le temps est VIRTUEL : test instantané
        val state = viewModel.uiState.value as RecipeListUiState.Success
        assertEquals("Result for chicken", state.recipes.single().name)
    }

    @Test
    fun `network error is exposed as Error state`() = runTest(mainDispatcherRule.dispatcher) {
        val repo = FakeRecipeRepository().apply { failure = PopoteException.Network() }
        val viewModel = RecipeListViewModel(repo)
        backgroundScope.launch { viewModel.uiState.collect {} }

        advanceTimeBy(301); runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state is RecipeListUiState.Error)
        assertEquals("Pas de connexion internet", (state as RecipeListUiState.Error).message)
    }
}
// endregion faketest
