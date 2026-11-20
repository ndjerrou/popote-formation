package fr.formation.kata

import fr.formation.kata.exos.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExosTest {
    @Test fun exo01() = assertTrue(e01().startsWith("Pâtes arrabiata pour 4 personnes"))
    @Test fun exo02() {
        assertEquals(20, cookingTime(20))
        assertEquals(32, cookingTime(20, portions = 6))
        assertEquals(20, cookingTime(baseMinutes = 10, extraPerPortion = 5, portions = 4))
    }
    @Test fun exo03() {
        assertEquals("Express", difficultyLabel(10)); assertEquals("Classique", difficultyLabel(30))
        assertEquals("Mijoté", difficultyLabel(90)); assertEquals("Week-end", difficultyLabel(300))
        assertEquals("Invalide", difficultyLabel(-1))
    }
    @Test fun exo04() {
        assertEquals(4, parsePortions("4")); assertEquals(12, parsePortions(" 40 "))
        assertEquals(1, parsePortions("abc")); assertEquals(1, parsePortions(null))
        assertEquals("Recette proposée par NISSIM", describeAuthor("nissim"))
        assertEquals("Auteur inconnu", describeAuthor(null))
    }
    @Test fun exo05() {
        val t = Timer("Pâtes", 3); t.tick(); t.tick(5)
        assertTrue(t.isDone)
        assertFailsWith<IllegalArgumentException> { Timer("x", -1) }
    }
    @Test fun exo06() {
        assertEquals(12, Portion.of(40).count)
        val a = IdGenerator.nextId(); assertEquals(a + 1, IdGenerator.nextId())
    }
    @Test fun exo07() = assertEquals("true / Review(author=Léa, stars=5, comment=) / Léa:5", reviewSummary())
    @Test fun exo08() {
        assertEquals("🥗 Starter", Category.STARTER.label())
        assertEquals("🌱 Vegan", Category.VEGAN.label())
    }
    @Test fun exo09() {
        assertEquals("✅ 2 recette(s)", render(LoadState.Success(2)))
        assertEquals("❌ Réseau (réessayer)", render(LoadState.Error("Réseau")))
    }
    @Test fun exo10() {
        assertEquals("spicy-arrabiata-penne", "Spicy Arrabiata Penne!".toSlug())
        assertEquals("45 min", 45.minutesLabel); assertEquals("2 h 15", 135.minutesLabel)
    }
    @Test fun exo11() {
        assertEquals(12, applyTwice(3, double))
        assertEquals(81, applyTwice(3) { it * it })
        assertEquals(23, applyTwice(3, ::plusTen))
    }
    @Test fun exo12() {
        val r = e12()
        assertEquals(listOf("Arrabiata", "Carbonara", "Salade"), r["rapides"])
        assertEquals(44.0, r["prixTotal"])
        assertEquals("Ratatouille", r["plusLong"])
    }
    @Test fun exo13() = assertEquals(listOf(250, -1, "Mesure invalide : 'abc'"), e13())
}
