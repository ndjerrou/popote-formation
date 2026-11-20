package fr.formation.recettes

import org.json.JSONObject

// Une recette : des données -> data class
data class Recette(
    val nom: String,
    val categorie: String,
    val origine: String,
    val instructions: String,
)

// L'état de l'écran de recherche : 4 cas, chacun avec ses données -> sealed interface
sealed interface EtatRecherche {
    data object Attente : EtatRecherche
    data object Chargement : EtatRecherche
    data class Succes(val recettes: List<Recette>) : EtatRecherche
    data class Erreur(val message: String) : EtatRecherche
}

// Extension : lire un texte dans un JSONObject, "" si la valeur est absente ou null
fun JSONObject.texte(cle: String): String = if (isNull(cle)) "" else getString(cle)

// Transforme la réponse JSON de TheMealDB en liste de Recette
fun lireRecettes(json: String): List<Recette> {
    val meals = JSONObject(json).optJSONArray("meals") ?: return emptyList()
    return (0 until meals.length()).map { i ->
        val meal = meals.getJSONObject(i)
        Recette(
            nom = meal.texte("strMeal"),
            categorie = meal.texte("strCategory"),
            origine = meal.texte("strArea"),
            instructions = meal.texte("strInstructions"),
        )
    }
}
