package fr.formation.kata.exos

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

// region E14
// E14 - suspend + async/await : séquentiel vs parallèle
suspend fun fetchRecipeName(id: Int): String {
    delay(1000)                 // suspend SANS bloquer le thread (≠ Thread.sleep)
    return "Recette #$id"
}

fun e14(): Pair<Long, Long> = runBlocking {
    val sequential = measureTimeMillis {
        fetchRecipeName(1); fetchRecipeName(2); fetchRecipeName(3)
    }
    val parallel = measureTimeMillis {
        val deferred = (1..3).map { id -> async { fetchRecipeName(id) } }
        deferred.awaitAll()
    }
    sequential to parallel      // ~3000 ms vs ~1000 ms
}
// endregion E14

// region E15
// E15 - launch, Job, annulation coopérative et timeout
fun e15(): List<String> = runBlocking {
    val log = mutableListOf<String>()
    val job = launch {
        repeat(100) { i ->
            log += "tick $i"
            delay(100)          // point de suspension = point d'annulation
        }
    }
    delay(350)
    job.cancelAndJoin()         // annule puis attend la fin propre
    log += "annulé"

    val result = withTimeoutOrNull(500) { fetchRecipeName(42) }   // 1000 ms > 500 ms
    log += "timeout -> $result"
    log
}
// endregion E15

// region E16
// E16 - Dispatchers : withContext pour changer de thread
suspend fun heavyNutritionScore(values: List<Int>): Int = withContext(Dispatchers.Default) {
    values.sumOf { it * it } % 100                       // calcul CPU -> Default
}

suspend fun readFakeFile(): String = withContext(Dispatchers.IO) {
    Thread.currentThread().name.substringBefore(" @")  // I/O (disque, réseau) -> IO
}
// endregion E16

// region E17
// E17 - Flow : flux froid, opérateurs, StateFlow
fun timerFlow(seconds: Int): Flow<Int> = flow {
    for (s in seconds downTo 0) {
        emit(s)
        delay(10)
    }
}

fun e17(): List<String> = runBlocking {
    timerFlow(5)
        .filter { it % 2 == 0 }
        .map { if (it == 0) "🔔 C'est prêt !" else "$it s" }
        .toList()
}

class Counter {
    private val _count = MutableStateFlow(0)      // privé et muable
    val count: StateFlow<Int> = _count.asStateFlow() // public et lecture seule
    fun increment() = _count.update { it + 1 }
}
// endregion E17

// region E18
// E18 - concurrence structurée : un échec annule les frères... sauf avec supervisorScope
fun e18(): List<String> = runBlocking {
    val log = mutableListOf<String>()
    try {
        coroutineScope {
            launch { delay(500); log += "jamais affiché" }
            launch { delay(100); error("API en panne") }
        }
    } catch (e: IllegalStateException) {
        log += "coroutineScope : ${e.message}"
    }
    supervisorScope {
        val handler = CoroutineExceptionHandler { _, e -> log += "géré : ${e.message}" }
        launch(handler) { error("image KO") }
        launch { delay(50); log += "le frère continue" }
    }
    log
}
// endregion E18
