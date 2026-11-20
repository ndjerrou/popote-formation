package fr.formation.musicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.formation.musicplayer.data.TrackCatalog
import fr.formation.musicplayer.feature.about.AboutScreen
import fr.formation.musicplayer.feature.library.LibraryScreen
import fr.formation.musicplayer.feature.player.PlayerRoute

// LE PLAN DE L'APPLI : quelles destinations existent, et comment on passe de l'une à l'autre.
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    // Le navController retient la pile des écrans visités (le "back stack")
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LibraryDestination,
        modifier = modifier,
    ) {
        composable<LibraryDestination> {
            LibraryScreen(
                tracks = TrackCatalog.tracks,
                onTrackClick = { track -> navController.navigate(PlayerDestination(trackId = track.id)) },
                onAboutClick = { navController.navigate(AboutDestination) },
            )
        }
        composable<PlayerDestination> {
            // Pas besoin de lire l'argument ici : le ViewModel le retrouve dans son SavedStateHandle
            PlayerRoute(onBack = { navController.popBackStack() })
        }
        composable<AboutDestination> {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
