package fr.formation.popote.domain

import java.util.Locale

// region model
/** Unités "normalisées" : on convertit kg -> g, l/cl -> ml pour pouvoir additionner. */
enum class MeasureUnit(val symbol: String) {
    GRAM("g"), MILLILITER("ml"), PIECE(""), TABLESPOON("c. à s."), TEASPOON("c. à c."), CUP("tasse");
}

/** Quantité additionnable (même unité uniquement). */
data class Quantity(val amount: Double, val unit: MeasureUnit) {
    operator fun plus(other: Quantity): Quantity {
        require(unit == other.unit) { "Unités incompatibles : $unit + ${other.unit}" }
        return copy(amount = amount + other.amount)
    }

    operator fun times(factor: Int): Quantity = copy(amount = amount * factor)
}

/** Une mesure TheMealDB ("200g", "1 tbsp", "Pinch"...) une fois analysée. */
sealed interface Measure {
    data class Exact(val quantity: Quantity) : Measure
    data class Vague(val text: String) : Measure      // "Pinch", "to taste"...
    data object None : Measure                         // champ vide
}

data class Ingredient(val name: String, val measure: Measure)

/** Version "liste" : l'API ne renvoie que l'id, le nom et la vignette. */
data class RecipeSummary(val id: String, val name: String, val thumbnailUrl: String?)

data class Recipe(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val thumbnailUrl: String?,
    val instructions: String,
    val ingredients: List<Ingredient>,
)

data class ShoppingItem(
    val name: String,
    val quantity: Quantity?,
    val notes: List<String> = emptyList(),
    val checked: Boolean = false,
)
// endregion model

// region format
/** Affichage lisible : 1500 g -> "1.5 kg", 3.0 pièces -> "3". */
fun Quantity.pretty(): String {
    // 2 décimales max, sans zéros inutiles ; Locale explicite (piège classique : "1.5" vs "1,5")
    fun Double.clean() = "%.2f".format(Locale.FRANCE, this).trimEnd('0').trimEnd(',')
    return when {
        unit == MeasureUnit.GRAM && amount >= 1000 -> "${(amount / 1000).clean()} kg"
        unit == MeasureUnit.MILLILITER && amount >= 1000 -> "${(amount / 1000).clean()} l"
        unit == MeasureUnit.PIECE -> amount.clean()
        else -> "${amount.clean()} ${unit.symbol}"
    }
}
// endregion format
