package fr.formation.mediahub.navigation

import kotlinx.serialization.Serializable

// Une destination = un type (navigation vérifiée par le compilateur)
@Serializable object MusiqueRoute
@Serializable object FilmsRoute
@Serializable object PodcastRoute
@Serializable data class FilmPlayerRoute(val filmId: Int)       // plein écran, hors barre de navigation

// Un onglet de la barre du bas : n'importe quelle route + un libellé + une icône
data class NavTab(val route: Any, val label: String, val icone: String)

val ONGLET_MUSIQUE = NavTab(MusiqueRoute, "Musique", "🎵")
val ONGLET_FILMS = NavTab(FilmsRoute, "Films", "🎬")
val ONGLET_PODCAST = NavTab(PodcastRoute, "Podcast", "🎙️")
