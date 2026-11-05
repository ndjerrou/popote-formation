package fr.formation.popote.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.formation.popote.R
import fr.formation.popote.ui.components.RecipeImage
import fr.formation.popote.ui.list.ErrorState
import org.koin.compose.viewmodel.koinViewModel

// region detailroute
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailRoute(
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effet de bord : se relance uniquement quand userMessage change
    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.recipe?.name ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(painterResource(R.drawable.ic_back), "Retour") }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            painterResource(if (state.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
                            contentDescription = "Favori",
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        val recipe = state.recipe
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { CircularProgressIndicator() }
            state.error != null -> Box(Modifier.padding(padding)) { ErrorState(state.error!!, viewModel::load) }
            recipe != null -> RecipeDetailContent(
                recipe = recipe,
                onAddToShoppingList = viewModel::addToShoppingList,
                modifier = Modifier.padding(padding),
                image = { RecipeImage(recipe.thumbnailUrl) },
            )
        }
    }
}
// endregion detailroute
