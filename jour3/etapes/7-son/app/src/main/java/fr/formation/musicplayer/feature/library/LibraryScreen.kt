package fr.formation.musicplayer.feature.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.formation.musicplayer.data.Track
import fr.formation.musicplayer.data.TrackCatalog
import fr.formation.musicplayer.ui.theme.MusicPlayerTheme

// La bibliothèque n'a pas d'état qui change : pas besoin de ViewModel, un écran stateless suffit.
@Composable
fun LibraryScreen(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("🎧 Ma bibliothèque", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onAboutClick) { Text("À propos") }
        }
        LazyColumn {
            items(tracks, key = { it.id }) { track ->
                ListItem(
                    headlineContent = { Text(track.title) },
                    supportingContent = { Text(track.artist) },
                    trailingContent = { Text("%d:%02d".format(track.durationSec / 60, track.durationSec % 60)) },
                    modifier = Modifier.clickable { onTrackClick(track) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryScreenPreview() {
    MusicPlayerTheme {
        LibraryScreen(tracks = TrackCatalog.tracks, onTrackClick = {}, onAboutClick = {})
    }
}
