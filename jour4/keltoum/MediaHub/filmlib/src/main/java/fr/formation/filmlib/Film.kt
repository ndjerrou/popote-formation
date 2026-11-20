package fr.formation.filmlib

// LE MÉTIER de l'équipe Film : des données et des règles, sans aucun écran.

data class Film(val id: Int, val titre: String, val realisateur: String, val dureeSec: Int, val synopsis: String)

object FilmCatalog {
    val films = listOf(
        Film(1, "Le Phare", "A. Morel", 95, "Un gardien de phare reçoit chaque nuit un message en morse."),
        Film(2, "Ville Basse", "S. Nakamura", 80, "Une ville construite sous un fleuve, et une panne d'électricité."),
        Film(3, "Dernier Train", "L. Okafor", 70, "Deux inconnus, un train de nuit, une valise qui n'est à personne."),
    )

    fun byId(id: Int): Film = films.firstOrNull { it.id == id }
        ?: throw IllegalArgumentException("Film inconnu : $id")
}

// La lecture (simulée) : une classe Kotlin pure, testable sans Android.
class LecteurFilm(val film: Film, position: Int = 0) {
    var position = position
        private set
    var enLecture = false
        private set
    val termine: Boolean get() = position >= film.dureeSec

    fun lectureOuPause() { if (!termine) enLecture = !enLecture }

    // Appelée chaque seconde par l'écran
    fun avancer() {
        if (!enLecture) return
        position = minOf(position + 1, film.dureeSec)
        if (termine) enLecture = false
    }
}

fun formatTemps(secondes: Int): String = "%d:%02d".format(secondes / 60, secondes % 60)
