package fr.formation.musicplayer.feature.player

import fr.formation.musicplayer.data.Track

// ① L'ÉTAT : tout ce que l'écran affiche, réuni dans UN seul objet immuable.
data class PlayerUiState(
    val track: Track,
    val isPlaying: Boolean = false,
    val positionSec: Int = 0,
    val favoriteIds: Set<Int> = emptySet(),
) {
    // Une valeur calculée à partir de l'état : on ne la stocke pas, on la déduit.
    val isFavorite: Boolean get() = track.id in favoriteIds
}

// ② LES ACTIONS : tout ce que l'utilisateur peut faire sur l'écran. Une liste fermée -> sealed interface.
sealed interface PlayerAction {
    data object PlayPause : PlayerAction                    // pas de donnée : un seul exemplaire suffit
    data object Next : PlayerAction
    data object Previous : PlayerAction
    data object ToggleFavorite : PlayerAction
    data class SeekTo(val positionSec: Int) : PlayerAction  // porte une donnée : la nouvelle position
}
