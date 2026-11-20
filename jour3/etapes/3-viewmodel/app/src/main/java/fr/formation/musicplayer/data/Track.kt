package fr.formation.musicplayer.data

// Un morceau de la playlist : des données -> data class
data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val durationSec: Int,
)

// Le catalogue : un seul exemplaire pour toute l'appli -> object
object TrackCatalog {
    val tracks = listOf(
        Track(1, "Aube sur le port", "Studio Sinus", 42),
        Track(2, "Pluie d'été", "Les Ondes Carrées", 37),
        Track(3, "Café du matin", "Trio Triangle", 45),
        Track(4, "Néons de minuit", "Duo Dents de Scie", 33),
    )
}
