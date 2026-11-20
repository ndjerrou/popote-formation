package fr.formation.collecte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import fr.formation.collecte.feature.collecte.CollecteRoute
import fr.formation.collecte.ui.theme.CollecteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollecteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CollecteRoute(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
