package fr.formation.mescourses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import fr.formation.mescourses.ui.theme.MesCoursesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MesCoursesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EcranCourses(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun EcranCourses(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val stockage = remember { Stockage(context) }

    // La liste est chargée depuis le stockage au démarrage
    var articles by remember { mutableStateOf(stockage.charger()) }

    // Le formulaire (rememberSaveable : survit à la rotation de l'écran)
    var nom by rememberSaveable { mutableStateOf("") }
    var quantite by rememberSaveable { mutableStateOf("1") }
    var rayon by rememberSaveable { mutableStateOf(Rayon.FRUITS_LEGUMES) }
    var erreur by rememberSaveable { mutableStateOf<String?>(null) }

    // Une seule porte d'entrée pour modifier la liste : on met à jour l'écran ET on sauvegarde
    fun mettreAJour(nouvelleListe: List<Article>) {
        articles = nouvelleListe
        stockage.sauver(nouvelleListe)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🛒 Mes courses", style = MaterialTheme.typography.headlineSmall)

        // ----- Le formulaire -----
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Article") },
                singleLine = true,
                modifier = Modifier.weight(2f)
            )
            OutlinedTextField(
                value = quantite,
                onValueChange = { quantite = it },
                label = { Text("Qté") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Rayon.entries.forEach { r ->
                FilterChip(
                    selected = r == rayon,
                    onClick = { rayon = r },
                    label = { Text(r.emoji) }
                )
            }
        }

        Button(
            onClick = {
                try {
                    val article = creerArticle(nom, quantite, rayon)
                    mettreAJour(articles + article)
                    nom = ""
                    quantite = "1"
                    erreur = null
                } catch (e: IllegalArgumentException) {
                    erreur = e.message
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter")
        }

        // erreur est nullable : on n'affiche le texte que s'il y en a un
        erreur?.let { message ->
            Text("⚠️ $message", color = MaterialTheme.colorScheme.error)
        }

        // ----- Le résumé (collections) -----
        val restants = articles.count { !it.achete }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "$restants article(s) à acheter sur ${articles.size}",
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { mettreAJour(articles.filter { !it.achete }) }) {
                Text("Retirer les achetés")
            }
        }

        // ----- La liste, triée par rayon -----
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(articles.sortedBy { it.rayon }) { article ->
                LigneArticle(
                    article = article,
                    onCocher = { coche ->
                        mettreAJour(articles.map { if (it == article) it.copy(achete = coche) else it })
                    },
                    onSupprimer = { mettreAJour(articles - article) }
                )
            }
        }
    }
}

@Composable
fun LigneArticle(article: Article, onCocher: (Boolean) -> Unit, onSupprimer: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = article.achete, onCheckedChange = onCocher)
        Text(
            text = "${article.rayon.emoji} ${article.nom} × ${article.quantite}",
            textDecoration = if (article.achete) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onSupprimer) {
            Text("🗑️")
        }
    }
}
