package fr.formation.popote.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.formation.popote.domain.RecipeRepository
import fr.formation.popote.domain.RecipeSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// region liststate
sealed interface RecipeListUiState {
    data object Loading : RecipeListUiState
    data class Success(val recipes: List<RecipeSummary>, val favoriteIds: Set<String>) : RecipeListUiState
    data class Error(val message: String) : RecipeListUiState
}
// endregion liststate

// region listvm
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class RecipeListViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    private val retryTrigger = MutableStateFlow(0)

    // query -> (debounce) -> recherche ; flatMapLatest ANNULE la recherche précédente
    private val searchResults = combine(_query.debounce(300), retryTrigger) { q, _ -> q }
        .flatMapLatest { q ->
            flow {
                emit(RecipeListUiState.Loading)
                val result = runCatching { repository.search(q) }
                emit(
                    result.fold(
                        onSuccess = { RecipeListUiState.Success(it, emptySet()) },
                        onFailure = { RecipeListUiState.Error(it.message ?: "Erreur inconnue") },
                    ),
                )
            }
        }

    val uiState: StateFlow<RecipeListUiState> =
        combine(searchResults, repository.observeFavorites()) { state, favorites ->
            if (state is RecipeListUiState.Success) state.copy(favoriteIds = favorites.map { it.id }.toSet())
            else state
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // survit 5 s (rotation)
            initialValue = RecipeListUiState.Loading,
        )

    fun onQueryChange(newQuery: String) { _query.value = newQuery }

    fun retry() = retryTrigger.value++

    fun toggleFavorite(recipe: RecipeSummary) {
        viewModelScope.launch { repository.toggleFavorite(recipe) }
    }
}
// endregion listvm
