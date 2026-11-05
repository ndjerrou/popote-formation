package fr.formation.popote.domain

// region shopping
/**
 * Règle métier centrale de Popote :
 * - on fusionne les ingrédients de plusieurs recettes (nom normalisé),
 * - on additionne les quantités de même unité,
 * - les mesures vagues ("Pinch") deviennent des notes, avec le nom de la recette.
 */
object ShoppingListBuilder {

    fun build(recipes: List<Recipe>): List<ShoppingItem> =
        recipes
            .flatMap { recipe -> recipe.ingredients.map { ingredient -> recipe.name to ingredient } }
            .groupBy { (_, ingredient) -> ingredient.name.normalized() }
            .flatMap { (name, entries) -> mergeEntries(name, entries) }
            .sortedBy { it.name }

    private fun mergeEntries(name: String, entries: List<Pair<String, Ingredient>>): List<ShoppingItem> {
        val totals: List<Quantity> = entries
            .mapNotNull { (_, ingredient) -> (ingredient.measure as? Measure.Exact)?.quantity }
            .groupBy { it.unit }
            .map { (_, quantities) -> quantities.reduce { acc, q -> acc + q } }

        val notes: List<String> = entries.mapNotNull { (recipeName, ingredient) ->
            (ingredient.measure as? Measure.Vague)?.let { "${it.text} ($recipeName)" }
        }

        if (totals.isEmpty()) return listOf(ShoppingItem(name, quantity = null, notes = notes))
        // Plusieurs unités pour un même ingrédient -> plusieurs lignes, notes sur la première
        return totals.mapIndexed { index, total ->
            ShoppingItem(name, total, if (index == 0) notes else emptyList())
        }
    }

    private fun String.normalized(): String = trim().lowercase()
}
// endregion shopping
