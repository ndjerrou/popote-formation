package fr.formation.collecte.feature.collecte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.formation.collecte.BuildConfig
import fr.formation.collecte.data.CsvExporter
import fr.formation.collecte.data.Releve
import fr.formation.collecte.export.MailExport
import fr.formation.collecte.ui.theme.CollecteTheme

@Composable
fun CollecteRoute(modifier: Modifier = Modifier, viewModel: CollecteViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    CollecteScreen(
        state = state,
        onAction = viewModel::onAction,
        onEnvoyer = { destinataire ->
            MailExport.envoyer(context, CsvExporter.toCsv(state.releves), destinataire, state.releves.size)
        },
        modifier = modifier,
    )
}

@Composable
fun CollecteScreen(
    state: CollecteUiState,
    onAction: (CollecteAction) -> Unit,
    onEnvoyer: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var site by rememberSaveable { mutableStateOf("") }
    var operateur by rememberSaveable { mutableStateOf("") }
    var mesure by rememberSaveable { mutableStateOf("") }
    var unite by rememberSaveable { mutableStateOf("") }
    var commentaire by rememberSaveable { mutableStateOf("") }
    var destinataire by rememberSaveable { mutableStateOf(BuildConfig.DEFAULT_RECIPIENT) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("📋 Collecte", style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(site, { site = it }, label = { Text("Site") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(operateur, { operateur = it }, label = { Text("Opérateur") }, singleLine = true, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                mesure, { mesure = it }, label = { Text("Mesure") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(unite, { unite = it }, label = { Text("Unité") }, singleLine = true, modifier = Modifier.weight(1f))
        }
        OutlinedTextField(commentaire, { commentaire = it }, label = { Text("Commentaire") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                onAction(CollecteAction.Ajouter(site, operateur, mesure, unite, commentaire))
                mesure = ""; commentaire = ""                      // on garde site et opérateur pour la saisie suivante
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Ajouter le relevé") }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { onAction(CollecteAction.ImporterDuCloud) }, enabled = !state.enChargement) { Text("☁️ Importer") }
            if (state.enChargement) CircularProgressIndicator(Modifier.padding(4.dp))
            TextButton(onClick = { onAction(CollecteAction.Vider) }, enabled = state.releves.isNotEmpty()) { Text("Vider") }
        }

        state.message?.let {
            Text(it, color = if (state.estErreur) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }

        OutlinedTextField(destinataire, { destinataire = it }, label = { Text("Destinataire") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { onEnvoyer(destinataire) },
            enabled = state.releves.isNotEmpty() && destinataire.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) { Text("📧 Envoyer ${state.releves.size} relevé(s) en CSV") }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(state.releves) { releve -> LigneReleve(releve, onSupprimer = { onAction(CollecteAction.Supprimer(releve)) }) }
        }
    }
}

@Composable
private fun LigneReleve(releve: Releve, onSupprimer: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${releve.site} · ${releve.mesure} ${releve.unite}", style = MaterialTheme.typography.titleSmall)
                Text("${releve.date} · ${releve.operateur}" + if (releve.commentaire.isNotEmpty()) " · ${releve.commentaire}" else "")
            }
            TextButton(onClick = onSupprimer) { Text("🗑️") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollecteScreenPreview() {
    CollecteTheme {
        CollecteScreen(
            state = CollecteUiState(releves = listOf(Releve("17/11/2026 08:30", "Entrepôt A", "Julie", 21.5, "°C"))),
            onAction = {},
            onEnvoyer = {},
        )
    }
}
