package fr.formation.popote.data.remote

import fr.formation.popote.domain.Ingredient
import fr.formation.popote.domain.MeasureParser
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.RecipeSummary
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

// region mapper
/** DTO réseau -> modèle Domain. Le reste de l'app ne voit JAMAIS le JSON. */
class MealMapper(private val json: Json) {

    fun toSummary(meal: JsonObject): RecipeSummary {
        val dto = json.decodeFromJsonElement(MealDto.serializer(), meal)
        return RecipeSummary(dto.idMeal, dto.strMeal, dto.strMealThumb)
    }

    fun toRecipe(meal: JsonObject): Recipe {
        val dto = json.decodeFromJsonElement(MealDto.serializer(), meal)
        // strIngredient1..20 / strMeasure1..20 : lecture dynamique
        val ingredients = (1..20).mapNotNull { i ->
            val name = meal.string("strIngredient$i")?.trim().orEmpty()
            if (name.isEmpty()) null
            else Ingredient(name, MeasureParser.parse(meal.string("strMeasure$i")))
        }
        return Recipe(
            id = dto.idMeal,
            name = dto.strMeal,
            category = dto.strCategory.orEmpty(),
            area = dto.strArea.orEmpty(),
            thumbnailUrl = dto.strMealThumb,
            instructions = dto.strInstructions.orEmpty(),
            ingredients = ingredients,
        )
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull
}
// endregion mapper
