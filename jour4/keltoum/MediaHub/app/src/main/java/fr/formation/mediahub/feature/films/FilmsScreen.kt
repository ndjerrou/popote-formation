package fr.formation.mediahub.feature.films

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.formation.filmlib.Film
import fr.formation.filmlib.FilmCatalog
import fr.formation.filmlib.formatTemps

// Un écran COMPOSE qui utilise le MÉTIER de la lib (FilmCatalog), sans toucher à ses écrans.
@Composable
fun FilmsScreen(onFilmClick: (Film) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("🎬 Films", style = MaterialTheme.typography.headlineSmall)
        Text("Fournis par la bibliothèque :filmlib", style = MaterialTheme.typography.bodySmall)
        LazyColumn {
            items(FilmCatalog.films, key = { it.id }) { film ->
                ListItem(
                    headlineContent = { Text(film.titre) },
                    supportingContent = { Text(film.realisateur) },
                    trailingContent = { Text(formatTemps(film.dureeSec)) },
                    modifier = Modifier.clickable { onFilmClick(film) },
                )
                HorizontalDivider()
            }
        }
    }
}
