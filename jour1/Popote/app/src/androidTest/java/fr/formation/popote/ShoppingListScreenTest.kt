package fr.formation.popote

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import fr.formation.popote.domain.MeasureUnit
import fr.formation.popote.domain.Quantity
import fr.formation.popote.domain.ShoppingItem
import fr.formation.popote.ui.shopping.ShoppingListScreen
import fr.formation.popote.ui.shopping.ShoppingUiState
import fr.formation.popote.ui.theme.PopoteTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// region uitest
/** Test instrumenté : s'exécute sur un émulateur/appareil (./gradlew connectedDebugAndroidTest). */
class ShoppingListScreenTest {

    @get:Rule val composeRule = createComposeRule()

    @Test
    fun clickingAnItemTogglesIt() {
        val pasta = ShoppingItem("pasta", Quantity(500.0, MeasureUnit.GRAM))
        var toggled: ShoppingItem? = null

        composeRule.setContent {
            PopoteTheme {
                ShoppingListScreen(ShoppingUiState(toBuy = listOf(pasta)), onToggle = { toggled = it }, onClearChecked = {})
            }
        }

        composeRule.onNodeWithText("500 g").assertIsDisplayed()
        composeRule.onNodeWithText("Pasta").performClick()
        assertEquals(pasta, toggled)
    }

    @Test
    fun emptyListShowsPlaceholder() {
        composeRule.setContent { PopoteTheme { ShoppingListScreen(ShoppingUiState(), {}, {}) } }
        composeRule.onNodeWithText("🛒 Liste vide : ajoutez une recette !").assertIsDisplayed()
    }
}
// endregion uitest
