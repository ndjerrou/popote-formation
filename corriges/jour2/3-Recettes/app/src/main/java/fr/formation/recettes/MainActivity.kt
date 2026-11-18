package fr.formation.recettes

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import fr.formation.recettes.ui.theme.RecettesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecettesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EcranRecettes(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun EcranRecettes(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("recettes", Context.MODE_PRIVATE) }

    var recherche by remember { mutableStateOf(prefs.getString("derniere", "") ?: "") }
    var etat by remember { mutableStateOf<EtatRecherche>(EtatRecherche.Attente) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("📖 Recettes du monde", style = MaterialTheme.typography.headlineSmall)

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = recherche,
                onValueChange = { recherche = it },
                label = { Text("Nom du plat (en anglais)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    prefs.edit { putString("derniere", recherche) }
                    scope.launch {
                        etat = EtatRecherche.Chargement
                        val resultat = runCatching { MealApi.chercher(recherche) }
                        etat = resultat.fold(
                            onSuccess = { recettes -> EtatRecherche.Succes(recettes) },
                            onFailure = { EtatRecherche.Erreur("Impossible de joindre le serveur") }
                        )
                    }
                },
                enabled = recherche.isNotBlank(),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Chercher")
            }
        }

        // Le when exhaustif : un affichage pour chaque cas de la sealed interface
        when (val e = etat) {
            EtatRecherche.Attente -> Text("Tapez un plat, par exemple « chicken » ou « cake ».")
            EtatRecherche.Chargement -> CircularProgressIndicator()
            is EtatRecherche.Erreur -> Text("⚠️ ${e.message}", color = MaterialTheme.colorScheme.error)
            is EtatRecherche.Succes -> {
                if (e.recettes.isEmpty()) {
                    Text("Aucune recette trouvée 😕")
                } else {
                    Text("${e.recettes.size} recette(s)")
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(e.recettes) { recette -> CarteRecette(recette) }
                    }
                }
            }
        }
    }
}

@Composable
fun CarteRecette(recette: Recette) {
    var ouverte by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { ouverte = !ouverte }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(recette.nom, style = MaterialTheme.typography.titleMedium)
            Text("${recette.categorie} · ${recette.origine}")
            if (ouverte) {
                Text(recette.instructions, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
