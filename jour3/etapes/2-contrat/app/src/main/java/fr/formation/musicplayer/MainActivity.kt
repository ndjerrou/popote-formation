package fr.formation.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.formation.musicplayer.data.TrackCatalog
import fr.formation.musicplayer.ui.theme.MusicPlayerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NaivePlayerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// 75 secondes -> "1:15"
fun formatTime(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

// ⚠️ ÉTAPE 0 : l'écran « tout-en-un ». Il marche… mais tout est mélangé ici.
@Composable
fun NaivePlayerScreen(modifier: Modifier = Modifier) {
    val tracks = TrackCatalog.tracks

    // L'état : 4 boîtes indépendantes, perdues à la rotation (remember)
    var index by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var position by remember { mutableIntStateOf(0) }
    var favoriteIds by remember { mutableStateOf(setOf<Int>()) }

    val track = tracks[index]
    val isFavorite = track.id in favoriteIds

    // La « lecture » : on avance d'une seconde par seconde tant que isPlaying est vrai
    LaunchedEffect(isPlaying, index) {
        while (isPlaying) {
            delay(1000)
            if (position < track.durationSec) {
                position++
            } else {
                index = (index + 1) % tracks.size   // fin du morceau : on passe au suivant
                position = 0
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())   // pour l'écran en paysage
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // La pochette
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🎵", fontSize = 96.sp)
        }

        Text(track.title, style = MaterialTheme.typography.headlineSmall)
        Text(track.artist, style = MaterialTheme.typography.bodyLarge)

        // La barre de progression : la logique (convertir, borner) est écrite dans l'interface
        Slider(
            value = position.toFloat(),
            onValueChange = { position = it.toInt() },
            valueRange = 0f..track.durationSec.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(position))
            Text(formatTime(track.durationSec))
        }

        // Les commandes : chaque bouton contient sa propre règle métier
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = {
                index = if (index == 0) tracks.lastIndex else index - 1
                position = 0
            }) { Text("⏮", fontSize = 28.sp) }

            Button(onClick = { isPlaying = !isPlaying }) {
                Text(if (isPlaying) "⏸ Pause" else "▶ Lecture", fontSize = 20.sp)
            }

            TextButton(onClick = {
                index = (index + 1) % tracks.size
                position = 0
            }) { Text("⏭", fontSize = 28.sp) }
        }

        TextButton(onClick = {
            favoriteIds = if (isFavorite) favoriteIds - track.id else favoriteIds + track.id
        }) {
            Text(if (isFavorite) "♥ Dans mes favoris" else "♡ Ajouter aux favoris", fontSize = 18.sp)
        }
    }
}
