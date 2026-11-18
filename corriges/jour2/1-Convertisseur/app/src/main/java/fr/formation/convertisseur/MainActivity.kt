package fr.formation.convertisseur

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.formation.convertisseur.ui.theme.ConvertisseurTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConvertisseurTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EcranConvertisseur(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun EcranConvertisseur(modifier: Modifier = Modifier) {
    // L'ÉTAT de l'écran : ce qui peut changer pendant que l'app tourne.
    var saisie by remember { mutableStateOf("") }
    var depuis by remember { mutableStateOf(Unite.GRAMME) }
    var vers by remember { mutableStateOf(Unite.ONCE) }
    var resultat by remember { mutableStateOf<Resultat?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🍳 Convertisseur de cuisine", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = saisie,
            onValueChange = { nouveauTexte -> saisie = nouveauTexte },
            label = { Text("Quantité") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("De :")
        ChoixUnite(selection = depuis, onChoix = { unite -> depuis = unite })

        Text("Vers :")
        ChoixUnite(selection = vers, onChoix = { unite -> vers = unite })

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { resultat = convertir(saisie, depuis, vers) }) {
                Text("Convertir")
            }
            TextButton(onClick = {
                val ancien = depuis
                depuis = vers
                vers = ancien
                resultat = null
            }) {
                Text("⇅ Inverser")
            }
        }

        // resultat est nullable : ?. puis ?: (la null safety du jour 1)
        Text(
            text = resultat?.texte() ?: "",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun ChoixUnite(selection: Unite, onChoix: (Unite) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Unite.entries.forEach { unite ->
            FilterChip(
                selected = unite == selection,
                onClick = { onChoix(unite) },
                label = { Text(unite.libelle) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EcranConvertisseurPreview() {
    ConvertisseurTheme {
        EcranConvertisseur()
    }
}
