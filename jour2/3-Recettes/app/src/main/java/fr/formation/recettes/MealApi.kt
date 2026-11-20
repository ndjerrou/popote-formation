package fr.formation.recettes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLEncoder

// Un seul client pour l'API : un object
object MealApi {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    // suspend : cette fonction peut "patienter" sans bloquer l'écran
    suspend fun chercher(nom: String): List<Recette> = withContext(Dispatchers.IO) {
        val url = BASE_URL + "search.php?s=" + URLEncoder.encode(nom, "UTF-8")
        val json = URL(url).readText()      // la requête HTTP GET
        lireRecettes(json)
    }
}
