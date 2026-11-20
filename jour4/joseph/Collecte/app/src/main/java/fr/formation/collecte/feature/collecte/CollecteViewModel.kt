package fr.formation.collecte.feature.collecte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.formation.collecte.BuildConfig
import fr.formation.collecte.data.CloudSource
import fr.formation.collecte.data.creerReleve
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollecteViewModel(
    private val cloud: CloudSource = CloudSource(BuildConfig.CLOUD_URL),
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollecteUiState())
    val uiState: StateFlow<CollecteUiState> = _uiState.asStateFlow()

    fun onAction(action: CollecteAction) {
        when (action) {
            is CollecteAction.Ajouter -> ajouter(action)
            is CollecteAction.Supprimer -> _uiState.update { it.copy(releves = it.releves - action.releve) }
            CollecteAction.ImporterDuCloud -> importer()
            CollecteAction.Vider -> _uiState.update { it.copy(releves = emptyList(), message = null) }
        }
    }

    private fun ajouter(a: CollecteAction.Ajouter) {
        try {
            val releve = creerReleve(a.site, a.operateur, a.mesure, a.unite, a.commentaire)
            _uiState.update { it.copy(releves = it.releves + releve, message = "✅ Relevé ajouté", estErreur = false) }
        } catch (e: IllegalArgumentException) {
            _uiState.update { it.copy(message = "⚠️ ${e.message}", estErreur = true) }
        }
    }

    private fun importer() {
        viewModelScope.launch {
            _uiState.update { it.copy(enChargement = true, message = null) }
            val resultat = runCatching { cloud.charger() }
            _uiState.update { etat ->
                resultat.fold(
                    onSuccess = { importes ->
                        etat.copy(enChargement = false, releves = etat.releves + importes,
                            message = "☁️ ${importes.size} relevé(s) importé(s)", estErreur = false)
                    },
                    onFailure = { etat.copy(enChargement = false, message = "⚠️ Import impossible : ${it.message}", estErreur = true) },
                )
            }
        }
    }
}
