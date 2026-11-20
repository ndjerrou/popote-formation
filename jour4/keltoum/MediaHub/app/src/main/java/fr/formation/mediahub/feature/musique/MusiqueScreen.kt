package fr.formation.mediahub.feature.musique

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

private const val TAG = "MediaHub"

@Composable
fun MusiqueScreen(podcastActif: Boolean, onPodcastActifChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    // Le cycle de vie, côté Compose : on l'observe sans écrire de Fragment
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { Log.d(TAG, "Musique : ON_RESUME (visible et active)") }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { Log.d(TAG, "Musique : ON_PAUSE") }
    DisposableEffect(Unit) {
        Log.d(TAG, "Musique : entre dans la composition")
        onDispose { Log.d(TAG, "Musique : quitte la composition") }
    }

    var enLecture by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text("🎵", fontSize = 72.sp)
        Text("Aube sur le port", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = { enLecture = !enLecture }) { Text(if (enLecture) "⏸ Pause" else "▶ Lecture") }

        Row(Modifier.fillMaxWidth().padding(top = 32.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Activer l'onglet Podcast", Modifier.weight(1f))
            Switch(checked = podcastActif, onCheckedChange = onPodcastActifChange)
        }
    }
}
