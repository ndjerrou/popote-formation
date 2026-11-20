package fr.formation.musicplayer.feature.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.formation.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun AboutScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack) { Text("← Retour") }
        Text("À propos", style = MaterialTheme.typography.headlineSmall)
        Text("MusicPlayer : l'appli fil rouge du jour 3 de la formation Kotlin & Android.")
        Text("Architecture : MVVM + MVI. Un ViewModel par écran, un état (UiState), des actions (Action).")
        Text("Les morceaux et les artistes sont fictifs : aucun droit d'auteur en jeu.")
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    MusicPlayerTheme { AboutScreen(onBack = {}) }
}
