package fr.formation.configsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import fr.formation.configsync.feature.sync.SyncRoute
import fr.formation.configsync.ui.theme.ConfigSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConfigSyncTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SyncRoute(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
