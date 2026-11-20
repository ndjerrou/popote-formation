package fr.formation.collecte.feature.collecte

import fr.formation.collecte.data.Releve

data class CollecteUiState(
    val releves: List<Releve> = emptyList(),
    val enChargement: Boolean = false,
    val message: String? = null,
    val estErreur: Boolean = false,
)

sealed interface CollecteAction {
    data class Ajouter(
        val site: String, val operateur: String, val mesure: String, val unite: String, val commentaire: String,
    ) : CollecteAction
    data class Supprimer(val releve: Releve) : CollecteAction
    data object ImporterDuCloud : CollecteAction
    data object Vider : CollecteAction
}
