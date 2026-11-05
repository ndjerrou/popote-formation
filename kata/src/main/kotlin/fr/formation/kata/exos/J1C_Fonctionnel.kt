package fr.formation.kata.exos

// region E11
// E11 - lambdas, fonctions d'ordre supérieur, référence de fonction
fun applyTwice(value: Int, transform: (Int) -> Int): Int = transform(transform(value))

fun e11(): List<Int> {
    val double: (Int) -> Int = { it * 2 }
    fun plusTen(x: Int) = x + 10
    return listOf(
        applyTwice(3, double),            // 12
        applyTwice(3) { it * it },        // 81  (trailing lambda)
        applyTwice(3, ::plusTen),         // 23  (référence de fonction)
    )
}
// endregion E11

// region E12
// E12 - collections : immuables par défaut, map/filter/groupBy/sumOf/sortedBy...
data class Dish(val name: String, val category: String, val minutes: Int, val price: Double)

val menu = listOf(
    Dish("Arrabiata", "Pâtes", 25, 9.5),
    Dish("Carbonara", "Pâtes", 20, 11.0),
    Dish("Ratatouille", "Légumes", 60, 10.0),
    Dish("Tiramisu", "Dessert", 30, 6.5),
    Dish("Salade", "Légumes", 10, 7.0),
)

fun e12(): Map<String, Any> = mapOf(
    "rapides" to menu.filter { it.minutes <= 25 }.map { it.name },
    "parCategorie" to menu.groupBy { it.category }.mapValues { (_, d) -> d.size },
    "prixTotal" to menu.sumOf { it.price },
    "plusLong" to (menu.maxByOrNull { it.minutes }?.name ?: "-"),
    "triParPrix" to menu.sortedBy { it.price }.map { it.name },
    "index" to menu.associateBy { it.name }.keys.first(),
    "auMoinsUnDessert" to menu.any { it.category == "Dessert" },
)
// endregion E12

// region E13
// E13 - exceptions : try est une expression, require/check, runCatching
class InvalidMeasureException(input: String) : IllegalArgumentException("Mesure invalide : '$input'")

fun parseGrams(input: String): Int {
    val number = input.removeSuffix("g").trim()
    return number.toIntOrNull() ?: throw InvalidMeasureException(input)
}

fun e13(): List<Any> {
    val a = try { parseGrams("250g") } catch (e: InvalidMeasureException) { -1 }
    val b = try { parseGrams("beaucoup") } catch (e: InvalidMeasureException) { -1 }
    val c = runCatching { parseGrams("abc") }
        .map { it * 2 }
        .getOrElse { error -> error.message ?: "?" }
    return listOf(a, b, c)
}
// endregion E13
