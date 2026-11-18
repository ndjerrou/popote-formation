package fr.formation.convertisseur

import kotlin.math.round

// Une liste FERMÉE d'unités : c'est une enum.
// Chaque unité connaît son libellé et son poids en grammes.
enum class Unite(val libelle: String, val enGrammes: Double) {
    GRAMME("g", 1.0),
    KILOGRAMME("kg", 1000.0),
    CUILLERE("c. à soupe", 15.0),
    ONCE("oz", 28.35),
    LIVRE("lb", 453.6),
}

// Le résultat d'une conversion : soit un succès (avec une valeur), soit une erreur (avec un message).
// Deux cas, chacun avec SES données : c'est une sealed interface.
sealed interface Resultat {
    data class Succes(val valeur: Double, val unite: Unite) : Resultat
    data class Erreur(val message: String) : Resultat
}

// La logique métier : aucune ligne d'interface ici.
fun convertir(saisie: String, depuis: Unite, vers: Unite): Resultat {
    val quantite = saisie.replace(',', '.').toDoubleOrNull()
        ?: return Resultat.Erreur("« $saisie » n'est pas un nombre")

    if (quantite < 0) {
        return Resultat.Erreur("La quantité doit être positive")
    }

    val grammes = quantite * depuis.enGrammes
    return Resultat.Succes(grammes / vers.enGrammes, vers)
}

// Fonction d'extension : on ajoute "joli()" au type Double.
// 1.5 -> "1,5" ; 2.0 -> "2" ; 0.3333 -> "0,33"
fun Double.joli(): String {
    val arrondi = round(this * 100) / 100
    return if (arrondi % 1.0 == 0.0) {
        arrondi.toInt().toString()
    } else {
        arrondi.toString().replace('.', ',')
    }
}

// Une autre extension, sur notre propre type : transformer un Resultat en texte à afficher.
fun Resultat.texte(): String = when (this) {
    is Resultat.Succes -> "= ${valeur.joli()} ${unite.libelle}"
    is Resultat.Erreur -> "⚠️ $message"
}
