package fr.formation.collecte.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Une saisie de l'opérateur : des données -> data class
data class Releve(
    val date: String,
    val site: String,
    val operateur: String,
    val mesure: Double,
    val unite: String,
    val commentaire: String = "",
)

// La validation, hors de l'écran : la fonction ne peut pas fabriquer un relevé invalide
fun creerReleve(site: String, operateur: String, mesureTexte: String, unite: String, commentaire: String): Releve {
    require(site.isNotBlank()) { "Le site est obligatoire" }
    require(operateur.isNotBlank()) { "L'opérateur est obligatoire" }
    val mesure = mesureTexte.replace(',', '.').toDoubleOrNull()          // « 21,5 » accepté
    require(mesure != null) { "La mesure doit être un nombre" }
    require(unite.isNotBlank()) { "L'unité est obligatoire" }
    val maintenant = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())
    return Releve(maintenant, site.trim(), operateur.trim(), mesure, unite.trim(), commentaire.trim())
}
