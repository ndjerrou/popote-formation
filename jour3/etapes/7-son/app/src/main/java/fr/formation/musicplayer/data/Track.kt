package fr.formation.musicplayer.data

import fr.formation.musicplayer.R

// Un morceau de la playlist : des données -> data class
data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val durationSec: Int,
    val audioRes: Int,               // le fichier son, rangé dans res/raw (R.raw.…)
)

// Le catalogue : un seul exemplaire pour toute l'appli -> object
object TrackCatalog {
    val tracks = listOf(
        Track(1, "Aube sur le port", "Studio Sinus", 42, R.raw.track_1),
        Track(2, "Pluie d'été", "Les Ondes Carrées", 37, R.raw.track_2),
        Track(3, "Café du matin", "Trio Triangle", 45, R.raw.track_3),
        Track(4, "Néons de minuit", "Duo Dents de Scie", 33, R.raw.track_4),
    )

    // Un id inconnu ? On retombe sur le premier morceau plutôt que de planter.
    fun byId(id: Int): Track = tracks.firstOrNull { it.id == id } ?: tracks.first()
}
