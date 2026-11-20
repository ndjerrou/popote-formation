package fr.formation.mediahub.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import fr.formation.mediahub.feature.films.FilmPlayerHost
import fr.formation.mediahub.feature.films.FilmsScreen
import fr.formation.mediahub.feature.musique.MusiqueScreen
import fr.formation.mediahub.feature.podcast.PodcastScreen

@Composable
fun AppNavHost(tabsViewModel: TabsViewModel = viewModel()) {
    val navController = rememberNavController()
    val onglets by tabsViewModel.onglets.collectAsStateWithLifecycle()
    val entreeCourante by navController.currentBackStackEntryAsState()
    val destination = entreeCourante?.destination

    // Le lecteur de film est en plein écran : pas de barre du bas
    val afficherBarre = destination?.hasRoute(FilmPlayerRoute::class) != true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (afficherBarre) {
                NavigationBar {
                    onglets.forEach { onglet ->
                        NavigationBarItem(
                            selected = destination?.hasRoute(onglet.route::class) == true,
                            onClick = {
                                navController.navigate(onglet.route) {
                                    // Le motif standard d'une barre d'onglets :
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true } // pas de pile infinie
                                    launchSingleTop = true                                                     // pas de doublon
                                    restoreState = true                                                        // on retrouve l'onglet tel qu'on l'a laissé
                                }
                            },
                            icon = { Text(onglet.icone) },
                            label = { Text(onglet.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MusiqueRoute,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<MusiqueRoute> {
                MusiqueScreen(
                    podcastActif = ONGLET_PODCAST in onglets,
                    onPodcastActifChange = tabsViewModel::activerPodcast,
                )
            }
            composable<FilmsRoute> {
                FilmsScreen(onFilmClick = { film -> navController.navigate(FilmPlayerRoute(film.id)) })
            }
            composable<PodcastRoute> { PodcastScreen() }
            composable<FilmPlayerRoute> { entree ->
                val route = entree.toRoute<FilmPlayerRoute>()
                FilmPlayerHost(filmId = route.filmId, onFermer = { navController.popBackStack() })
            }
        }
    }
}
