package fr.formation.popote.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

// region api
/** Retrofit génère l'implémentation de cette interface à l'exécution. */
interface MealDbApi {
    @GET("search.php")
    suspend fun search(@Query("s") query: String): MealsResponse

    @GET("lookup.php")
    suspend fun lookup(@Query("i") id: String): MealsResponse

    companion object {
        const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
}

/** {"meals": [...]} ou {"meals": null} quand rien n'est trouvé. */
@Serializable
data class MealsResponse(val meals: List<JsonObject>? = null)

/** Les champs "simples" d'un repas, décodés de façon typée. */
@Serializable
data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null,
    val strMealThumb: String? = null,
)
// endregion api
