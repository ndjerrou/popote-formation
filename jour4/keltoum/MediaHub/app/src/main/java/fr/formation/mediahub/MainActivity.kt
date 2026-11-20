package fr.formation.mediahub

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import fr.formation.mediahub.navigation.AppNavHost
import fr.formation.mediahub.ui.theme.MediaHubTheme

// ⚠️ FragmentActivity, et pas ComponentActivity : un Fragment a besoin d'un FragmentManager.
// (FragmentActivity hérite de ComponentActivity : setContent { } marche toujours.)
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaHubTheme {
                AppNavHost()
            }
        }
    }
}
