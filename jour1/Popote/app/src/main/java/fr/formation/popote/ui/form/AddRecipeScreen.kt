package fr.formation.popote.ui.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.formation.popote.R
import org.koin.compose.viewmodel.koinViewModel
import fr.formation.popote.domain.Ingredient
import fr.formation.popote.domain.Measure
import fr.formation.popote.domain.MeasureParser
import fr.formation.popote.domain.pretty
import fr.formation.popote.ui.theme.PopoteTheme

// region formstate
/** L'état du formulaire : une data class immuable, qu'on remplace par copy(). */
data class RecipeFormState(
    val name: String = "",
    val category: String = "",
    val ingredientName: String = "",
    val ingredientMeasure: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val showErrors: Boolean = false,
) {
    val nameError: Boolean get() = showErrors && name.isBlank()
    val ingredientsError: Boolean get() = showErrors && ingredients.isEmpty()
    val isValid: Boolean get() = name.isNotBlank() && ingredients.isNotEmpty()

    /** Aperçu en direct du parsing de la quantité : la logique métier du J1 réutilisée ! */
    val measurePreview: String
        get() = when (val m = MeasureParser.parse(ingredientMeasure)) {
            is Measure.Exact -> "→ ${m.quantity.pretty()}"
            is Measure.Vague -> "→ mesure libre"
            Measure.None -> ""
        }

    fun addIngredient(): RecipeFormState =
        if (ingredientName.isBlank()) this
        else copy(
            ingredients = ingredients + Ingredient(ingredientName.trim(), MeasureParser.parse(ingredientMeasure)),
            ingredientName = "",
            ingredientMeasure = "",
        )

    fun removeIngredient(index: Int) = copy(ingredients = ingredients.filterIndexed { i, _ -> i != index })
}
// endregion formstate

// region formstateful
/** Version "stateful" (J3) : l'état vit dans le ViewModel, le formulaire reste stateless. */
@Composable
fun AddRecipeRoute(
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRecipeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AddRecipeForm(
        state = state,
        onStateChange = viewModel::onStateChange,
        onSave = { if (viewModel.save()) onSaved() },
        modifier = modifier,
    )
}
// endregion formstateful

// region formstateless
/** Version "stateless" (state hoisting) : facile à prévisualiser et à tester. */
@Composable
fun AddRecipeForm(
    state: RecipeFormState,
    onStateChange: (RecipeFormState) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.form_title), style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = state.name,
            onValueChange = { onStateChange(state.copy(name = it)) },
            label = { Text(stringResource(R.string.form_name)) },
            isError = state.nameError,
            supportingText = { if (state.nameError) Text(stringResource(R.string.form_error_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.category,
            onValueChange = { onStateChange(state.copy(category = it)) },
            label = { Text(stringResource(R.string.form_category)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.ingredientName,
                onValueChange = { onStateChange(state.copy(ingredientName = it)) },
                label = { Text(stringResource(R.string.form_ingredient)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = state.ingredientMeasure,
                onValueChange = { onStateChange(state.copy(ingredientMeasure = it)) },
                label = { Text(stringResource(R.string.form_measure)) },
                supportingText = { Text(state.measurePreview) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }
        FilledTonalButton(onClick = { onStateChange(state.addIngredient()) }) {
            Text(stringResource(R.string.form_add_ingredient))
        }
        if (state.ingredientsError) {
            Text(stringResource(R.string.form_error_ingredients), color = MaterialTheme.colorScheme.error)
        }
        state.ingredients.forEachIndexed { index, ingredient ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("• ${ingredient.name}", modifier = Modifier.weight(1f))
                IconButton(onClick = { onStateChange(state.removeIngredient(index)) }) {
                    Icon(painterResource(R.drawable.ic_delete), contentDescription = "Supprimer")
                }
            }
        }
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.form_save))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddRecipeFormPreview() {
    PopoteTheme {
        AddRecipeForm(
            state = RecipeFormState(name = "Crêpes", ingredientMeasure = "250g", showErrors = true),
            onStateChange = {},
            onSave = {},
        )
    }
}
// endregion formstateless
