package fr.formation.mediahub.feature.films

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.compose.AndroidFragment
import fr.formation.filmlib.FilmPlayerFragment

// LE PONT : afficher le Fragment de l'autre équipe dans notre écran Compose, sans le réécrire.
@Composable
fun FilmPlayerHost(filmId: Int, onFermer: () -> Unit, modifier: Modifier = Modifier) {
    AndroidFragment<FilmPlayerFragment>(
        modifier = modifier.fillMaxSize(),
        arguments = FilmPlayerFragment.arguments(filmId),       // ① on passe les données au Fragment
    ) { fragment ->
        fragment.onFermer = onFermer                            // ② le Fragment nous rappelle quand il veut se fermer
    }
    // ③ Le bouton Retour : d'abord le Fragment (s'il a un synopsis ouvert), sinon la navigation Compose
    //   revient à la liste des films. Rien à écrire ici.
}
