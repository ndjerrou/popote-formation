package fr.formation.popote.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.formation.popote.domain.ShoppingItem
import fr.formation.popote.domain.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// region shoppingvm
data class ShoppingUiState(val toBuy: List<ShoppingItem> = emptyList(), val done: List<ShoppingItem> = emptyList()) {
    val progress: Float get() = if (toBuy.isEmpty() && done.isEmpty()) 0f else done.size.toFloat() / (toBuy.size + done.size)
}

class ShoppingListViewModel(private val repository: ShoppingRepository) : ViewModel() {

    val uiState: StateFlow<ShoppingUiState> = repository.observeItems()
        .map { items ->
            val (done, toBuy) = items.partition { it.checked }   // destructuring d'une Pair
            ShoppingUiState(toBuy, done)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShoppingUiState())

    fun toggle(item: ShoppingItem) = viewModelScope.launch { repository.toggle(item) }

    fun clearChecked() = viewModelScope.launch { repository.clearChecked() }
}
// endregion shoppingvm
