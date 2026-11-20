package fr.formation.configsync.data

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// Signe une requête avec la clé PRIVÉE (HMAC-SHA256).
// La clé privée ne voyage jamais : on n'envoie que la signature.
// Le serveur, qui connaît la même clé privée, refait le calcul et compare.
object RequestSigner {

    // Le texte signé : horodatage + méthode + chemin, un par ligne.
    // ⚠️ Il doit être construit EXACTEMENT comme côté serveur (à vérifier dans la doc de l'API).
    fun payload(timestamp: Long, method: String, path: String): String = "$timestamp\n$method\n$path"

    fun sign(privateKey: String, timestamp: Long, method: String, path: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(privateKey.toByteArray(), "HmacSHA256"))
        val bytes = mac.doFinal(payload(timestamp, method, path).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }      // en hexadécimal
    }
}
