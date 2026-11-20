package fr.formation.popote.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.formation.popote.R
import fr.formation.popote.data.FakeRecipes
import fr.formation.popote.domain.RecipeSummary
import fr.formation.popote.domain.toSummary
import fr.formation.popote.ui.components.RecipeImage
import fr.formation.popote.ui.theme.PopoteTheme
import org.koin.compose.viewmodel.koinViewModel

// region listroute
/** "Route" : relie le ViewModel (Koin) à l'écran stateless. */
@Composable
fun RecipeListRoute(
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()   // Flow -> State Compose
    val query by viewModel.query.collectAsStateWithLifecycle()
    RecipeListScreen(
        state = state,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onRecipeClick = onRecipeClick,
        onFavoriteClick = viewModel::toggleFavorite,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}
// endregion listroute

private val suggestions = listOf("Chicken", "Pasta", "Salmon", "Beef", "Cake", "Soup")

// region listscreen
@Composable
fun RecipeListScreen(
    state: RecipeListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (RecipeSummary) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), null) },
            placeholder = { Text("Rechercher une recette…") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )
        // LazyRow : liste horizontale "paresseuse"
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(suggestions) { s -> SuggestionChip(onClick = { onQueryChange(s) }, label = { Text(s) }) }
        }
        when (state) {
            RecipeListUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is RecipeListUiState.Error -> ErrorState(state.message, onRetry)
            is RecipeListUiState.Success ->
                if (state.recipes.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Aucune recette pour « $query »") }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // key = identifiant stable : Compose recycle et anime correctement
                        items(state.recipes, key = { it.id }) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                isFavorite = recipe.id in state.favoriteIds,
                                onClick = { onRecipeClick(recipe.id) },
                                onFavoriteClick = { onFavoriteClick(recipe) },
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun RecipeCard(recipe: RecipeSummary, isFavorite: Boolean, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RecipeImage(recipe.thumbnailUrl, Modifier.size(72.dp), height = 72.dp)
            Text(
                recipe.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            )
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    painterResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
                    contentDescription = "Favori",
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}
// endregion listscreen

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("😕 $message", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Icon(painterResource(R.drawable.ic_refresh), null)
            Text("Réessayer", Modifier.padding(start = 8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeListPreview() {
    PopoteTheme {
        RecipeListScreen(
            state = RecipeListUiState.Success(FakeRecipes.all.map { it.toSummary() }, setOf("52771")),
            query = "", onQueryChange = {}, onRecipeClick = {}, onFavoriteClick = {}, onRetry = {},
        )
    }
}
