package fr.formation.configsync.feature.sync

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.formation.configsync.BuildConfig
import fr.formation.configsync.data.ApiClient
import fr.formation.configsync.data.IniWriter
import fr.formation.configsync.storage.Destination
import fr.formation.configsync.storage.FileStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// AndroidViewModel : un ViewModel qui reçoit l'Application (un Context qui vit aussi longtemps que l'appli).
// Il en a besoin pour écrire des fichiers.
class SyncViewModel(application: Application) : AndroidViewModel(application) {

    private val api = ApiClient(BuildConfig.API_BASE_URL, BuildConfig.API_PUBLIC_KEY, BuildConfig.API_PRIVATE_KEY)
    private val files = FileStore(application)
    private val prefs = application.getSharedPreferences("configsync", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        SyncUiState(hasSdCard = files.hasSdCard(), chosenFolder = prefs.getString(KEY_FOLDER, null))
    )
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()

    fun onAction(action: SyncAction) {
        when (action) {
            is SyncAction.ChooseDestination -> _uiState.update { it.copy(destination = action.destination, message = null) }
            is SyncAction.FolderChosen -> rememberFolder(action.uri)
            SyncAction.Sync -> sync()
        }
    }

    // Le dossier choisi via SAF : on demande à Android de garder l'autorisation après redémarrage
    private fun rememberFolder(uri: String) {
        getApplication<Application>().contentResolver.takePersistableUriPermission(
            uri.toUri(),
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
        )
        prefs.edit { putString(KEY_FOLDER, uri) }
        _uiState.update { it.copy(chosenFolder = uri, message = null) }
    }

    private fun sync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val result = runCatching {
                val sections = api.fetchPairs()                                  // ① réseau
                val now = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE).format(Date())
                val ini = IniWriter.write(sections, "Généré par ConfigSync le $now")  // ② format
                ini to write(CONFIG_FILE, ini)                                   // ③ fichier
            }
            _uiState.update { state ->
                result.fold(
                    onSuccess = { (ini, path) -> state.copy(isLoading = false, iniPreview = ini, message = "✅ Écrit : $path", isError = false) },
                    onFailure = { e -> state.copy(isLoading = false, message = "⚠️ ${e.message ?: e::class.simpleName}", isError = true) },
                )
            }
        }
    }

    private fun write(name: String, text: String): String {
        val state = _uiState.value
        return when (state.destination) {
            Destination.PHONE_APP -> files.writeInPhoneAppDir(name, text)
            Destination.SD_APP -> files.writeInSdAppDir(name, text)
            Destination.DOCUMENTS -> files.writeInDocuments(name, text)
            Destination.SD_ROOT -> {
                val folder = state.chosenFolder ?: throw IllegalStateException("Choisis d'abord le dossier de la carte SD")
                files.writeInChosenFolder(folder.toUri(), name, text)
            }
        }
    }

    companion object {
        private const val CONFIG_FILE = "config.ini"
        private const val KEY_FOLDER = "chosen_folder"
    }
}
