package fr.formation.mescourses

// Les rayons du magasin : une liste fermée -> enum
enum class Rayon(val emoji: String) {
    FRUITS_LEGUMES("🥕"),
    FRAIS("🧀"),
    EPICERIE("🥫"),
    AUTRE("🛒"),
}

// Un article de la liste : des données -> data class
data class Article(
    val nom: String,
    val quantite: Int,
    val rayon: Rayon,
    val achete: Boolean = false,
)

// Valide le formulaire et fabrique un Article.
// Si une saisie est mauvaise, require lève une IllegalArgumentException avec le message.
fun creerArticle(nom: String, quantiteTexte: String, rayon: Rayon): Article {
    require(nom.isNotBlank()) { "Le nom est obligatoire" }
    require(!nom.contains(";")) { "Le caractère ; est interdit" }

    val quantite = quantiteTexte.toIntOrNull()
    require(quantite != null) { "La quantité doit être un nombre entier" }
    require(quantite > 0) { "La quantité doit être supérieure à 0" }

    return Article(nom.trim(), quantite, rayon)
}

// ----- Transformation en texte, pour le stockage (fonctions d'extension) -----

// Article -> "Tomates;3;FRUITS_LEGUMES;false"
fun Article.versLigne(): String = "$nom;$quantite;${rayon.name};$achete"

// "Tomates;3;FRUITS_LEGUMES;false" -> Article (ou null si la ligne est abîmée)
fun String.versArticle(): Article? {
    val morceaux = split(";")
    if (morceaux.size != 4) return null
    return try {
        Article(
            nom = morceaux[0],
            quantite = morceaux[1].toInt(),          // peut lever NumberFormatException
            rayon = Rayon.valueOf(morceaux[2]),      // peut lever IllegalArgumentException
            achete = morceaux[3].toBoolean(),
        )
    } catch (e: IllegalArgumentException) {
        null                                         // ligne illisible : on l'ignore
    }
}
