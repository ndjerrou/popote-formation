package fr.formation.configsync.feature.sync

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.formation.configsync.storage.Destination
import fr.formation.configsync.ui.theme.ConfigSyncTheme

@Composable
fun SyncRoute(modifier: Modifier = Modifier, viewModel: SyncViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // L'écran système « Choisir un dossier » (Storage Access Framework)
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) viewModel.onAction(SyncAction.FolderChosen(uri.toString()))
    }
    SyncScreen(state, viewModel::onAction, onPickFolder = { folderPicker.launch(null) }, modifier)
}

@Composable
fun SyncScreen(
    state: SyncUiState,
    onAction: (SyncAction) -> Unit,
    onPickFolder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("🔄 ConfigSync", style = MaterialTheme.typography.headlineSmall)
        Text("Où écrire config.ini ?", style = MaterialTheme.typography.titleMedium)

        Destination.entries.forEach { destination ->
            val enabled = state.hasSdCard || destination != Destination.SD_APP
            Row(
                modifier = Modifier.fillMaxWidth().selectable(
                    selected = state.destination == destination,
                    enabled = enabled,
                    onClick = { onAction(SyncAction.ChooseDestination(destination)) },
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = state.destination == destination, onClick = null, enabled = enabled)
                Column(Modifier.padding(start = 8.dp)) {
                    Text(destination.label)
                    Text(
                        if (enabled) destination.detail else "Pas de carte SD détectée",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        if (state.destination == Destination.SD_ROOT) {
            OutlinedButton(onClick = onPickFolder) {
                Text(if (state.chosenFolder == null) "Choisir la racine de la carte SD" else "Changer de dossier")
            }
        }

        Button(
            onClick = { onAction(SyncAction.Sync) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Synchroniser") }

        if (state.isLoading) CircularProgressIndicator()
        state.message?.let { message ->
            Text(
                message,
                color = if (state.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }
        if (state.iniPreview.isNotEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Text(state.iniPreview, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(12.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncScreenPreview() {
    ConfigSyncTheme {
        SyncScreen(
            state = SyncUiState(hasSdCard = true, iniPreview = "[reseau]\nport=502\n", message = "✅ Écrit : config.ini"),
            onAction = {},
            onPickFolder = {},
        )
    }
}
