package fr.formation.kata

import fr.formation.kata.exos.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoroutinesTest {
    @Test fun exo14() { val (s, p) = e14(); assertTrue(s >= 3000 && p < 2000) }
    @Test fun exo15() {
        val log = e15()
        assertEquals(listOf("tick 0", "tick 1", "tick 2", "tick 3", "annulé", "timeout -> null"), log)
    }
    @Test fun exo16() = runTest {
        assertEquals(14, heavyNutritionScore(listOf(1, 2, 3)))
        assertTrue(readFakeFile().contains("DefaultDispatcher-worker"))
    }
    @Test fun exo17() {
        assertEquals(listOf("4 s", "2 s", "🔔 C'est prêt !"), e17())
        val c = Counter(); c.increment(); c.increment(); assertEquals(2, c.count.value)
    }
    @Test fun exo18() = assertEquals(
        listOf("coroutineScope : API en panne", "géré : image KO", "le frère continue"), e18(),
    )
}
