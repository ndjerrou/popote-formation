package fr.formation.popote.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.RecipeRepository
import fr.formation.popote.domain.ShoppingRepository
import fr.formation.popote.domain.toSummary
import fr.formation.popote.ui.navigation.RecipeDetailRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// region detailvm
data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val recipe: Recipe? = null,
    val isFavorite: Boolean = false,
    val error: String? = null,
    val userMessage: String? = null,   // message "one-shot" (snackbar)
)

class RecipeDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipes: RecipeRepository,
    private val shopping: ShoppingRepository,
) : ViewModel() {

    // L'argument de navigation est lu depuis le SavedStateHandle (survit à la mort du process)
    private val recipeId: String = savedStateHandle.toRoute<RecipeDetailRoute>().recipeId

    private val _state = MutableStateFlow(RecipeDetailUiState())

    val uiState: StateFlow<RecipeDetailUiState> =
        combine(_state, recipes.observeFavorites()) { state, favorites ->
            state.copy(isFavorite = favorites.any { it.id == recipeId })
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecipeDetailUiState())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { recipes.getRecipe(recipeId) }
                .onSuccess { recipe -> _state.update { it.copy(isLoading = false, recipe = recipe) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun toggleFavorite() {
        val recipe = _state.value.recipe ?: return
        viewModelScope.launch { recipes.toggleFavorite(recipe.toSummary()) }
    }

    fun addToShoppingList() {
        val recipe = _state.value.recipe ?: return
        viewModelScope.launch {
            shopping.addRecipe(recipe)
            _state.update { it.copy(userMessage = "${recipe.ingredients.size} ingrédients ajoutés aux courses") }
        }
    }

    fun onMessageShown() = _state.update { it.copy(userMessage = null) }
}
// endregion detailvm
