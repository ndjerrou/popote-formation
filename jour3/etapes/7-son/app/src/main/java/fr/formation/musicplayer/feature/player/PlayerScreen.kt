package fr.formation.musicplayer.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.formation.musicplayer.data.TrackCatalog
import fr.formation.musicplayer.ui.theme.MusicPlayerTheme

// LA ROUTE : le seul endroit qui connaît le ViewModel. Elle fait le lien, rien d'autre.
@Composable
fun PlayerRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlayerScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,                  // la navigation ne passe pas par le ViewModel
        modifier = modifier,
    )
}

// L'ÉCRAN : sans état (stateless). Il reçoit l'état, il émet des actions. Rien d'autre.
@Composable
fun PlayerScreen(
    state: PlayerUiState,
    onAction: (PlayerAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
            Text("← Bibliothèque")
        }
        PlayerContent(state = state, onAction = onAction)
    }
}

@Composable
private fun PlayerContent(state: PlayerUiState, onAction: (PlayerAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🎵", fontSize = 96.sp)
        }

        Text(state.track.title, style = MaterialTheme.typography.headlineSmall)
        Text(state.track.artist, style = MaterialTheme.typography.bodyLarge)

        Slider(
            value = state.positionSec.toFloat(),
            onValueChange = { onAction(PlayerAction.SeekTo(it.toInt())) },
            valueRange = 0f..state.track.durationSec.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(state.positionSec))
            Text(formatTime(state.track.durationSec))
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = { onAction(PlayerAction.Previous) }) { Text("⏮", fontSize = 28.sp) }
            Button(onClick = { onAction(PlayerAction.PlayPause) }) {
                Text(if (state.isPlaying) "⏸ Pause" else "▶ Lecture", fontSize = 20.sp)
            }
            TextButton(onClick = { onAction(PlayerAction.Next) }) { Text("⏭", fontSize = 28.sp) }
        }

        TextButton(onClick = { onAction(PlayerAction.ToggleFavorite) }) {
            Text(if (state.isFavorite) "♥ Dans mes favoris" else "♡ Ajouter aux favoris", fontSize = 18.sp)
        }
    }
}

// 75 secondes -> "1:15"
private fun formatTime(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

// Les aperçus : on fabrique l'état à la main. Aucun ViewModel, aucune coroutine.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PlayerScreenPlayingPreview() {
    MusicPlayerTheme {
        PlayerScreen(
            state = PlayerUiState(track = TrackCatalog.tracks[0], isPlaying = true, positionSec = 17),
            onAction = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PlayerScreenFavoritePreview() {
    MusicPlayerTheme {
        PlayerScreen(
            state = PlayerUiState(track = TrackCatalog.tracks[2], favoriteIds = setOf(3)),
            onAction = {},
            onBack = {},
        )
    }
}
