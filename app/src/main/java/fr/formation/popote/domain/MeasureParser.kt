package fr.formation.popote.domain

// region parser
object MeasureParser {
    // "200g", "1.5 kg", "1/2 cup", "3 cloves"
    private val pattern = Regex("""^(\d+/\d+|\d+(?:[.,]\d+)?)\s*([a-zA-Z]*)""")

    // unité texte -> (unité normalisée, facteur de conversion)
    private val units: Map<String, Pair<MeasureUnit, Double>> = buildMap {
        listOf("g", "gr", "gram", "grams").forEach { put(it, MeasureUnit.GRAM to 1.0) }
        listOf("kg", "kilo", "kilos").forEach { put(it, MeasureUnit.GRAM to 1000.0) }
        listOf("lb", "lbs", "pound", "pounds").forEach { put(it, MeasureUnit.GRAM to 453.6) }
        listOf("oz", "ounce", "ounces").forEach { put(it, MeasureUnit.GRAM to 28.35) }
        listOf("ml").forEach { put(it, MeasureUnit.MILLILITER to 1.0) }
        listOf("cl").forEach { put(it, MeasureUnit.MILLILITER to 10.0) }
        listOf("l", "litre", "liter", "litres", "liters").forEach { put(it, MeasureUnit.MILLILITER to 1000.0) }
        listOf("tbsp", "tbs", "tablespoon", "tablespoons").forEach { put(it, MeasureUnit.TABLESPOON to 1.0) }
        listOf("tsp", "teaspoon", "teaspoons").forEach { put(it, MeasureUnit.TEASPOON to 1.0) }
        listOf("cup", "cups").forEach { put(it, MeasureUnit.CUP to 1.0) }
        listOf("", "clove", "cloves", "large", "medium", "small", "whole", "slice", "slices")
            .forEach { put(it, MeasureUnit.PIECE to 1.0) }
    }

    fun parse(raw: String?): Measure {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return Measure.None

        val match = pattern.find(text) ?: return Measure.Vague(text)
        val (number, unitText) = match.destructured
        val amount = parseNumber(number) ?: return Measure.Vague(text)
        val (unit, factor) = units[unitText.lowercase()] ?: return Measure.Vague(text)
        return Measure.Exact(Quantity(amount * factor, unit))
    }

    private fun parseNumber(s: String): Double? =
        if ("/" in s) {
            val (n, d) = s.split("/").map { it.toDoubleOrNull() ?: return null }
            if (d == 0.0) null else n / d
        } else {
            s.replace(',', '.').toDoubleOrNull()
        }
}
// endregion parser
