package fr.formation.configsync.feature.sync

import fr.formation.configsync.storage.Destination

// L'état de l'écran, en un seul objet
data class SyncUiState(
    val destination: Destination = Destination.PHONE_APP,
    val hasSdCard: Boolean = false,
    val chosenFolder: String? = null,      // l'URI du dossier choisi (cas ④), en texte
    val isLoading: Boolean = false,
    val iniPreview: String = "",
    val message: String? = null,
    val isError: Boolean = false,
)

// Tout ce que l'utilisateur peut faire
sealed interface SyncAction {
    data class ChooseDestination(val destination: Destination) : SyncAction
    data class FolderChosen(val uri: String) : SyncAction
    data object Sync : SyncAction
}
