package fr.formation.filmlib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LecteurFilmTest {
    private val film = Film(9, "Test", "Moi", 2, "")

    @Test
    fun `en pause, le temps ne bouge pas`() {
        val lecteur = LecteurFilm(film)
        lecteur.avancer()
        assertEquals(0, lecteur.position)
    }

    @Test
    fun `a la fin, la lecture s'arrete`() {
        val lecteur = LecteurFilm(film)
        lecteur.lectureOuPause()
        repeat(5) { lecteur.avancer() }
        assertEquals(2, lecteur.position)
        assertTrue(lecteur.termine)
        assertFalse(lecteur.enLecture)
    }
}
