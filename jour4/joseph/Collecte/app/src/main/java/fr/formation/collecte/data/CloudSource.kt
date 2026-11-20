package fr.formation.collecte.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

// Récupère des relevés déjà saisis ailleurs (ici un JSON en ligne ; demain, la vraie API ou un export de formulaire)
class CloudSource(private val url: String) {
    suspend fun charger(): List<Releve> = withContext(Dispatchers.IO) {
        val tableau = JSONArray(URL(url).readText())
        (0 until tableau.length()).map { i ->
            val o = tableau.getJSONObject(i)
            Releve(
                date = o.getString("date"),
                site = o.getString("site"),
                operateur = o.getString("operateur"),
                mesure = o.getDouble("mesure"),
                unite = o.getString("unite"),
                commentaire = o.optString("commentaire"),
            )
        }
    }
}
