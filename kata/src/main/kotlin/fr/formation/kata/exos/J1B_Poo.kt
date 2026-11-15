package fr.formation.kata.exos

// region E05
// E05 - Classe : constructeur primaire, propriété calculée, init, visibilité
class Timer(val label: String, totalSeconds: Int) {
    init {
        require(totalSeconds >= 0) { "Durée négative : $totalSeconds" }
    }

    var remaining: Int = totalSeconds
        private set                      // lecture publique, écriture privée

    val isDone: Boolean
        get() = remaining == 0           // propriété calculée

    fun tick(seconds: Int = 1) {
        remaining = (remaining - seconds).coerceAtLeast(0)
    }

    override fun toString() = "$label : ${remaining}s"
}
// endregion E05

// region E06
// E06 - object (singleton) et companion object (≈ static Java)
object IdGenerator {
    private var next = 1
    fun nextId(): Int = next++
}

class Portion private constructor(val count: Int) {
    companion object {
        const val MAX = 12
        fun of(count: Int): Portion = Portion(count.coerceIn(1, MAX)) // factory
    }
}
// endregion E06

// region E07
// E07 - data class : equals/hashCode/toString/copy/destructuring gratuits
data class Review(val author: String, val stars: Int, val comment: String = "")

fun reviewSummary(): String {
    val r1 = Review("Léa", 4)
    val r2 = r1.copy(stars = 5)           // nouvelle instance, r1 inchangé
    val (author, stars) = r2              // déstructuration
    return "${r1 == Review("Léa", 4)} / $r2 / $author:$stars"
}
// reviewSummary() → "true / Review(author=Léa, stars=5, comment=) / Léa:5"
// endregion E07

// region E08
// E08 - enum class avec propriétés et méthodes
enum class Category(val emoji: String, val vegetarian: Boolean) {
    STARTER("🥗", true), MAIN("🍝", false), DESSERT("🍰", true), VEGAN("🌱", true);

    // "STARTER" → "S" + "tarter" → "Starter"
    fun label() = "$emoji ${name.take(1)}${name.drop(1).lowercase()}"
}
// Category.STARTER.label() → "🥗 Starter" · Category.VEGAN.vegetarian → true
// endregion E08

// region E09
// E09 - sealed interface : hiérarchie fermée, when exhaustif sans else
sealed interface LoadState {
    data object Loading : LoadState
    data class Success(val count: Int) : LoadState
    data class Error(val message: String, val retryable: Boolean = true) : LoadState
}

fun render(state: LoadState): String = when (state) {
    LoadState.Loading -> "⏳ Chargement…"
    is LoadState.Success -> "✅ ${state.count} recette(s)"   // smart cast
    is LoadState.Error -> "❌ ${state.message}" + if (state.retryable) " (réessayer)" else ""
}
// endregion E09

// region E10
// E10 - fonctions d'extension (et propriété d'extension)
fun String.toSlug(): String =
    lowercase().trim().replace(Regex("[^a-z0-9]+"), "-").trim('-')

val Int.minutesLabel: String
    get() = if (this < 60) "$this min" else "${this / 60} h ${(this % 60).toString().padStart(2, '0')}"

// "Spicy Arrabiata Penne!".toSlug() → "spicy-arrabiata-penne" · 45.minutesLabel → "45 min" · 135.minutesLabel → "2 h 15"
// endregion E10
