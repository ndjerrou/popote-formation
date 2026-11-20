package fr.formation.popote

import fr.formation.popote.data.remote.MealMapper
import fr.formation.popote.data.remote.MealsResponse
import fr.formation.popote.domain.Measure
import fr.formation.popote.domain.MeasureUnit
import fr.formation.popote.domain.Quantity
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class MealMapperTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `maps TheMealDB json to domain recipe`() {
        val body = """
            {"meals":[{"idMeal":"52771","strMeal":"Spicy Arrabiata Penne","strCategory":"Vegetarian",
            "strArea":"Italian","strInstructions":"Boil.","strMealThumb":"https://x/y.jpg","strTags":null,
            "strIngredient1":"penne rigate","strMeasure1":"1 pound",
            "strIngredient2":"garlic","strMeasure2":"3 cloves",
            "strIngredient3":"","strMeasure3":"","strIngredient4":null,"strMeasure4":null}]}
        """.trimIndent()

        val response = json.decodeFromString(MealsResponse.serializer(), body)
        val recipe = MealMapper(json).toRecipe(response.meals!!.first())

        assertEquals("Spicy Arrabiata Penne", recipe.name)
        assertEquals(2, recipe.ingredients.size)          // les champs vides/null sont ignorés
        assertEquals(Measure.Exact(Quantity(3.0, MeasureUnit.PIECE)), recipe.ingredients[1].measure)
    }

    @Test
    fun `null meals means no result`() {
        val response = json.decodeFromString(MealsResponse.serializer(), """{"meals":null}""")
        assertEquals(null, response.meals)
    }
}
