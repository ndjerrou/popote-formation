package fr.formation.kata

import fr.formation.kata.exos.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExosTest {
    @Test fun exo01() = assertTrue(e01().startsWith("Pâtes arrabiata pour 4 personnes"))
    @Test fun exo02() = assertEquals(listOf(20, 32, 20), e02())
    @Test fun exo03() = assertEquals(listOf("Express", "Classique", "Mijoté", "Week-end", "Invalide"), e03())
    @Test fun exo04() = assertEquals(listOf(4, 12, 1, 1, "Recette proposée par NISSIM", "Auteur inconnu"), e04())
    @Test fun exo05() {
        val t = Timer("Pâtes", 3); t.tick(); t.tick(5)
        assertTrue(t.isDone)
        assertFailsWith<IllegalArgumentException> { Timer("x", -1) }
    }
    @Test fun exo06() {
        assertEquals(12, Portion.of(40).count)
        val a = IdGenerator.nextId(); assertEquals(a + 1, IdGenerator.nextId())
    }
    @Test fun exo07() = assertEquals(listOf(true, Review("Léa", 5), "Léa:5"), e07())
    @Test fun exo08() = assertEquals(3, e08().size)
    @Test fun exo09() {
        assertEquals("✅ 2 recette(s)", render(LoadState.Success(listOf("a", "b"))))
        assertEquals("❌ Réseau (réessayer)", render(LoadState.Error("Réseau")))
    }
    @Test fun exo10() = assertEquals(listOf("spicy-arrabiata-penne", "45 min", "2 h 15"), e10())
    @Test fun exo11() = assertEquals(listOf(12, 81, 23), e11())
    @Test fun exo12() {
        val r = e12()
        assertEquals(listOf("Arrabiata", "Carbonara", "Salade"), r["rapides"])
        assertEquals(44.0, r["prixTotal"])
        assertEquals("Ratatouille", r["plusLong"])
    }
    @Test fun exo13() = assertEquals(listOf(250, -1, "Mesure invalide : 'abc'"), e13())
}
