package fr.formation.popote.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// region addvm
/** J3 : l'état du formulaire quitte le Composable -> il survit à la rotation. */
class AddRecipeViewModel(private val shopping: ShoppingRepository) : ViewModel() {

    private val _state = MutableStateFlow(RecipeFormState())
    val state: StateFlow<RecipeFormState> = _state.asStateFlow()

    fun onStateChange(newState: RecipeFormState) { _state.value = newState }

    /** Renvoie true si la recette est valide et a été envoyée vers la liste de courses. */
    fun save(): Boolean {
        val form = _state.value
        if (!form.isValid) {
            _state.update { it.copy(showErrors = true) }
            return false
        }
        val recipe = Recipe(
            id = "custom-${System.currentTimeMillis()}",
            name = form.name.trim(),
            category = form.category.ifBlank { "Perso" },
            area = "Maison",
            thumbnailUrl = null,
            instructions = "",
            ingredients = form.ingredients,
        )
        viewModelScope.launch { shopping.addRecipe(recipe) }
        _state.value = RecipeFormState()
        return true
    }
}
// endregion addvm
