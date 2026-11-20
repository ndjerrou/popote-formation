package fr.formation.popote.data

import fr.formation.popote.domain.Ingredient
import fr.formation.popote.domain.MeasureParser
import fr.formation.popote.domain.Recipe

// region fake
/** Données factices (J2-J3) : on construit l'UI AVANT de brancher le réseau. */
object FakeRecipes {
    private fun ingredients(vararg pairs: Pair<String, String>) =
        pairs.map { (name, measure) -> Ingredient(name, MeasureParser.parse(measure)) }

    val all: List<Recipe> = listOf(
        Recipe(
            id = "52771", name = "Spicy Arrabiata Penne", category = "Vegetarian", area = "Italian",
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg",
            instructions = "Faire bouillir l'eau, cuire les pâtes. Faire revenir l'ail dans l'huile, " +
                "ajouter les tomates et le piment, laisser mijoter 5 min. Mélanger avec les pâtes.",
            ingredients = ingredients(
                "penne rigate" to "1 pound", "olive oil" to "1/4 cup", "garlic" to "3 cloves",
                "chopped tomatoes" to "400g", "red chilli flakes" to "1/2 tsp", "basil" to "Pinch",
            ),
        ),
        Recipe(
            id = "52772", name = "Teriyaki Chicken Casserole", category = "Chicken", area = "Japanese",
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
            instructions = "Préchauffer le four à 180°C. Mélanger la sauce, napper le poulet, enfourner 30 min.",
            ingredients = ingredients(
                "soy sauce" to "3/4 cup", "water" to "1/2 cup", "garlic" to "2 cloves",
                "chicken breasts" to "2", "rice" to "300g",
            ),
        ),
        Recipe(
            id = "52959", name = "Baked salmon with fennel", category = "Seafood", area = "British",
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/1548772327.jpg",
            instructions = "Émincer le fenouil, le disposer dans un plat, poser le saumon, cuire 20 min.",
            ingredients = ingredients(
                "fennel" to "2 medium", "olive oil" to "2 tbsp", "salmon" to "4 fillets", "lemon" to "1",
            ),
        ),
    )

    fun byId(id: String): Recipe? = all.firstOrNull { it.id == id }
}
// endregion fake
