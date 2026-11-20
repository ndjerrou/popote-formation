package fr.formation.kata

import fr.formation.popote.domain.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MeasureParserTest {
    @Test fun grams() = assertEquals(Measure.Exact(Quantity(200.0, MeasureUnit.GRAM)), MeasureParser.parse("200g"))
    @Test fun kilosAreConverted() = assertEquals(Measure.Exact(Quantity(1500.0, MeasureUnit.GRAM)), MeasureParser.parse("1,5 kg"))
    @Test fun fraction() = assertEquals(Measure.Exact(Quantity(0.5, MeasureUnit.CUP)), MeasureParser.parse("1/2 cup"))
    @Test fun pieces() = assertEquals(Measure.Exact(Quantity(3.0, MeasureUnit.PIECE)), MeasureParser.parse("3 cloves"))
    @Test fun pounds() = assertEquals(Measure.Exact(Quantity(453.6, MeasureUnit.GRAM)), MeasureParser.parse("1 pound"))
    @Test fun vague() = assertEquals(Measure.Vague("Pinch"), MeasureParser.parse("Pinch"))
    @Test fun unknownUnitIsVague() = assertEquals(Measure.Vague("2 handfuls"), MeasureParser.parse("2 handfuls"))
    @Test fun empty() { assertEquals(Measure.None, MeasureParser.parse("")); assertEquals(Measure.None, MeasureParser.parse(null)) }
}

class QuantityTest {
    @Test fun plusSameUnit() = assertEquals(Quantity(300.0, MeasureUnit.GRAM), Quantity(100.0, MeasureUnit.GRAM) + Quantity(200.0, MeasureUnit.GRAM))
    @Test fun plusDifferentUnitFails() { assertFailsWith<IllegalArgumentException> { Quantity(1.0, MeasureUnit.GRAM) + Quantity(1.0, MeasureUnit.CUP) } }
    @Test fun pretty() {
        assertEquals("1,5 kg", Quantity(1500.0, MeasureUnit.GRAM).pretty())
        assertEquals("3", Quantity(3.0, MeasureUnit.PIECE).pretty())
        assertEquals("250 ml", Quantity(250.0, MeasureUnit.MILLILITER).pretty())
        assertEquals("0,25 tasse", Quantity(0.25, MeasureUnit.CUP).pretty())
    }
}

class ShoppingListBuilderTest {
    private fun recipe(name: String, vararg ingredients: Pair<String, String>) = Recipe(
        id = name, name = name, category = "", area = "", thumbnailUrl = null, instructions = "",
        ingredients = ingredients.map { (n, m) -> Ingredient(n, MeasureParser.parse(m)) },
    )

    @Test fun mergesSameIngredientAcrossRecipes() {
        val items = ShoppingListBuilder.build(listOf(
            recipe("A", "Garlic" to "2 cloves", "Pasta" to "200g"),
            recipe("B", "garlic " to "1", "Pasta" to "0.3kg", "Salt" to "Pinch"),
        ))
        assertEquals(listOf("garlic", "pasta", "salt"), items.map { it.name })
        assertEquals(Quantity(3.0, MeasureUnit.PIECE), items[0].quantity)
        assertEquals(Quantity(500.0, MeasureUnit.GRAM), items[1].quantity)
        assertEquals(null, items[2].quantity)
        assertEquals(listOf("Pinch (B)"), items[2].notes)
    }

    @Test fun differentUnitsGiveSeveralLines() {
        val items = ShoppingListBuilder.build(listOf(recipe("A", "Milk" to "200ml", "Milk" to "1 cup")))
        assertEquals(2, items.size)
    }
}

class ShoppingMergeTest {
    @Test fun mergeAddsAndUnchecks() {
        val existing = listOf(ShoppingItem("pasta", Quantity(200.0, MeasureUnit.GRAM), checked = true))
        val added = listOf(ShoppingItem("pasta", Quantity(100.0, MeasureUnit.GRAM)), ShoppingItem("salt", null, listOf("Pinch")))
        val merged = ShoppingListBuilder.merge(existing, added)
        assertEquals(listOf(
            ShoppingItem("pasta", Quantity(300.0, MeasureUnit.GRAM), checked = false),
            ShoppingItem("salt", null, listOf("Pinch")),
        ), merged)
    }
}
