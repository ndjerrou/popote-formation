package fr.formation.popote.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.formation.popote.domain.ReminderSettings
import fr.formation.popote.domain.ShoppingItem
import fr.formation.popote.domain.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// region shoppingvm
data class ShoppingUiState(
    val toBuy: List<ShoppingItem> = emptyList(),
    val done: List<ShoppingItem> = emptyList(),
    val reminderEnabled: Boolean = false,
) {
    val progress: Float get() = if (toBuy.isEmpty() && done.isEmpty()) 0f else done.size.toFloat() / (toBuy.size + done.size)
}

class ShoppingListViewModel(
    private val repository: ShoppingRepository,
    private val reminders: ReminderSettings,
) : ViewModel() {

    val uiState: StateFlow<ShoppingUiState> =
        combine(repository.observeItems(), reminders.enabled) { items, reminderEnabled ->
            val (done, toBuy) = items.partition { it.checked }   // destructuring d'une Pair
            ShoppingUiState(toBuy, done, reminderEnabled)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShoppingUiState())

    fun toggle(item: ShoppingItem) = viewModelScope.launch { repository.toggle(item) }

    fun clearChecked() = viewModelScope.launch { repository.clearChecked() }

    fun setReminder(enabled: Boolean) = viewModelScope.launch { reminders.setEnabled(enabled) }

    fun testReminder() = reminders.runNow()
}
// endregion shoppingvm
