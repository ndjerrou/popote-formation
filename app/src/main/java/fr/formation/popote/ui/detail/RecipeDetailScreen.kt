package fr.formation.popote.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.formation.popote.R
import fr.formation.popote.data.FakeRecipes
import fr.formation.popote.domain.Ingredient
import fr.formation.popote.domain.Measure
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.pretty
import fr.formation.popote.ui.theme.PopoteTheme

// region detail
/** Composable "stateless" : il reçoit des données et des callbacks, rien d'autre. */
@Composable
fun RecipeDetailContent(
    recipe: Recipe,
    onAddToShoppingList: () -> Unit,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit = { RecipeImagePlaceholder() },
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        image()
        Text(recipe.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(recipe.category) })
            AssistChip(onClick = {}, label = { Text(recipe.area) })
        }
        HorizontalDivider()
        Text(
            text = stringResource(R.string.ingredients) + " · " +
                pluralStringResource(R.plurals.ingredient_count, recipe.ingredients.size, recipe.ingredients.size),
            style = MaterialTheme.typography.titleMedium,
        )
        recipe.ingredients.forEach { IngredientRow(it) }
        Button(onClick = onAddToShoppingList, modifier = Modifier.fillMaxWidth()) {
            Icon(painterResource(R.drawable.ic_cart), contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.add_to_cart))
        }
        HorizontalDivider()
        Text(stringResource(R.string.instructions), style = MaterialTheme.typography.titleMedium)
        Text(recipe.instructions, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("• ${ingredient.name}")
        Text(
            text = when (val m = ingredient.measure) {      // when exhaustif sur une sealed interface
                is Measure.Exact -> m.quantity.pretty()
                is Measure.Vague -> m.text
                Measure.None -> ""
            },
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun RecipeImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.large),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_restaurant),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailPreview() {
    PopoteTheme { RecipeDetailContent(recipe = FakeRecipes.all.first(), onAddToShoppingList = {}) }
}
// endregion detail
