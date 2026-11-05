package fr.formation.popote

import fr.formation.popote.domain.MeasureUnit
import fr.formation.popote.domain.Quantity
import fr.formation.popote.domain.ReminderSettings
import fr.formation.popote.domain.ShoppingItem
import fr.formation.popote.domain.ShoppingRepository
import fr.formation.popote.ui.shopping.ShoppingListViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// region mockktest
@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val pasta = ShoppingItem("pasta", Quantity(500.0, MeasureUnit.GRAM))
    private val salt = ShoppingItem("salt", null, checked = true)

    // relaxed = true : les fonctions non configurées renvoient une valeur par défaut
    private val repository = mockk<ShoppingRepository>(relaxed = true)
    private val reminders = mockk<ReminderSettings>(relaxed = true)

    @Test
    fun `separates items to buy and items already in the cart`() = runTest {
        // GIVEN
        every { repository.observeItems() } returns MutableStateFlow(listOf(pasta, salt))
        every { reminders.enabled } returns flowOf(true)
        val viewModel = ShoppingListViewModel(repository, reminders)

        // WHEN : on collecte uiState (sinon WhileSubscribed ne démarre jamais)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        // THEN
        val state = viewModel.uiState.value
        assertEquals(listOf(pasta), state.toBuy)
        assertEquals(listOf(salt), state.done)
        assertEquals(0.5f, state.progress)
        assertEquals(true, state.reminderEnabled)
    }

    @Test
    fun `toggle delegates to the repository`() = runTest {
        every { repository.observeItems() } returns flowOf(emptyList())
        every { reminders.enabled } returns flowOf(false)
        val viewModel = ShoppingListViewModel(repository, reminders)

        viewModel.toggle(pasta)
        viewModel.testReminder()

        coVerify(exactly = 1) { repository.toggle(pasta) }
        verify { reminders.runNow() }
    }
}
// endregion mockktest
