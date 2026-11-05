package fr.formation.popote.ui.shopping

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material3.Switch
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.formation.popote.domain.ShoppingItem
import fr.formation.popote.domain.pretty
import org.koin.compose.viewmodel.koinViewModel

// region permission
@Composable
fun ShoppingListRoute(modifier: Modifier = Modifier, viewModel: ShoppingListViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Permission runtime (Android 13+) : on demande, puis on agit selon la réponse
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> viewModel.setReminder(granted) }

    Column(modifier) {
        ReminderRow(
            enabled = state.reminderEnabled,
            onEnabledChange = { wanted ->
                val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED
                if (wanted && needsPermission) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                else viewModel.setReminder(wanted)
            },
            onTestClick = viewModel::testReminder,
        )
        ShoppingListScreen(state, onToggle = { viewModel.toggle(it) }, onClearChecked = { viewModel.clearChecked() })
    }
}
// endregion permission

@Composable
private fun ReminderRow(enabled: Boolean, onEnabledChange: (Boolean) -> Unit, onTestClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("Rappel quotidien", modifier = Modifier.weight(1f))
        if (enabled) TextButton(onClick = onTestClick) { Text("Tester") }
        Switch(checked = enabled, onCheckedChange = onEnabledChange)
    }
}

// region shoppingscreen

@Composable
fun ShoppingListScreen(
    state: ShoppingUiState,
    onToggle: (ShoppingItem) -> Unit,
    onClearChecked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.toBuy.isEmpty() && state.done.isEmpty()) {
        Box(modifier.fillMaxSize(), Alignment.Center) { Text("🛒 Liste vide : ajoutez une recette !") }
        return
    }
    Column(modifier.fillMaxSize()) {
        LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth().padding(16.dp))
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(state.toBuy, key = { "${it.name}-${it.quantity?.unit}" }) { ShoppingRow(it, onToggle) }
            if (state.done.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Dans le panier (${state.done.size})", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                        TextButton(onClick = onClearChecked) { Text("Vider") }
                    }
                }
                items(state.done, key = { "done-${it.name}-${it.quantity?.unit}" }) { ShoppingRow(it, onToggle) }
            }
        }
    }
}

@Composable
private fun ShoppingRow(item: ShoppingItem, onToggle: (ShoppingItem) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onToggle(item) }, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = item.checked, onCheckedChange = { onToggle(item) })
        Column(Modifier.weight(1f)) {
            Text(
                item.name.replaceFirstChar { it.uppercase() },
                textDecoration = if (item.checked) TextDecoration.LineThrough else null,
            )
            if (item.notes.isNotEmpty()) {
                Text(item.notes.joinToString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
        Text(item.quantity?.pretty() ?: "", color = MaterialTheme.colorScheme.primary)
    }
}
// endregion shoppingscreen
