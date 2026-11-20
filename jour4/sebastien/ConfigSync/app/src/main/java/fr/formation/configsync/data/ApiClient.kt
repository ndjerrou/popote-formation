package fr.formation.configsync.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Une erreur renvoyée par le serveur (401 = clé ou signature refusée, 404 = mauvaise adresse…)
class ApiException(val code: Int, message: String) : Exception("HTTP $code : $message")

// Les paires reçues, rangées par section : "reseau" -> { "port" -> "502", … }
typealias Sections = Map<String, Map<String, String>>

class ApiClient(
    private val baseUrl: String,
    private val publicKey: String,
    private val privateKey: String,
) {
    suspend fun fetchPairs(): Sections = withContext(Dispatchers.IO) {
        val path = "/pairs"
        val timestamp = System.currentTimeMillis() / 1000          // en secondes
        val connection = URL(baseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            // La clé PUBLIQUE dit « qui je suis », la SIGNATURE prouve que je possède la clé privée.
            connection.setRequestProperty("X-Api-Key", publicKey)
            connection.setRequestProperty("X-Timestamp", timestamp.toString())
            connection.setRequestProperty("X-Signature", RequestSigner.sign(privateKey, timestamp, "GET", path))

            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                val error = connection.errorStream?.bufferedReader()?.readText().orEmpty()
                throw ApiException(code, error)
            }
            parseSections(connection.inputStream.bufferedReader().readText())
        } finally {
            connection.disconnect()
        }
    }
}

// { "nom": "x", "reseau": { "port": 502 } }  ->  [general] nom=x  +  [reseau] port=502
fun parseSections(json: String): Sections {
    val root = JSONObject(json)
    val general = linkedMapOf<String, String>()
    val sections = linkedMapOf<String, Map<String, String>>()
    for (key in root.keys()) {
        val value = root.get(key)
        if (value is JSONObject) {
            sections[key] = value.keys().asSequence().associateWith { value.get(it).toString() }
        } else {
            general[key] = value.toString()
        }
    }
    return if (general.isEmpty()) sections else mapOf("general" to general) + sections
}
