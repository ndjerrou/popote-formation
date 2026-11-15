package fr.formation.kata.exos

// region E01
// E01 - val / var / types / templates
fun e01(): String {
    val recipeName = "Pâtes arrabiata"      // immuable (préférez val par défaut)
    var portions = 2                        // muable
    portions += 2
    val pricePerPortion: Double = 1.75      // type explicite (optionnel ici)
    val total = portions * pricePerPortion  // Int * Double -> Double
    return "$recipeName pour $portions personnes : $total €"   // 7.0 € (formatage FR : voir pretty())
}
// endregion E01

// region E02
// E02 - fonctions : paramètres par défaut, arguments nommés, expression body
fun cookingTime(baseMinutes: Int, portions: Int = 2, extraPerPortion: Int = 3): Int =
    baseMinutes + (portions - 2).coerceAtLeast(0) * extraPerPortion

// cookingTime(20)                                               → 20
// cookingTime(20, portions = 6)                                 → 32
// cookingTime(baseMinutes = 10, extraPerPortion = 5, portions = 4) → 20
// endregion E02

// region E03
// E03 - when (expression), ranges, if expression
fun difficultyLabel(minutes: Int): String = when (minutes) {
    in 0..15 -> "Express"
    in 16..45 -> "Classique"
    in 46..120 -> "Mijoté"
    else -> if (minutes < 0) "Invalide" else "Week-end"
}

// difficultyLabel(10) → "Express" · difficultyLabel(300) → "Week-end" · difficultyLabel(-1) → "Invalide"
// endregion E03

// region E04
// E04 - Null safety : ?, ?., ?:, let, !! (à éviter)
fun parsePortions(input: String?): Int {
    // toIntOrNull() renvoie Int? ; l'Elvis ?: donne la valeur par défaut
    val parsed = input?.trim()?.toIntOrNull() ?: return 1
    return parsed.coerceIn(1, 12)
}

fun describeAuthor(author: String?): String =
    author?.let { "Recette proposée par ${it.uppercase()}" } ?: "Auteur inconnu"

// parsePortions("4") → 4 · parsePortions(" 40 ") → 12 · parsePortions("abc") → 1 · parsePortions(null) → 1
// describeAuthor("nissim") → "Recette proposée par NISSIM" · describeAuthor(null) → "Auteur inconnu"
// endregion E04
