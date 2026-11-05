package fr.formation.popote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import fr.formation.popote.data.FakeRecipes
import fr.formation.popote.legacy.XmlDemoActivity
import fr.formation.popote.ui.detail.RecipeDetailContent
import fr.formation.popote.ui.form.AddRecipeScreen
import fr.formation.popote.ui.theme.PopoteTheme

// region lifecycle
private const val TAG = "Popote"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate (savedInstanceState = $savedInstanceState)")
        enableEdgeToEdge()
        setContent {
            PopoteTheme { PopoteJ2App() }
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { Log.d(TAG, "onPause"); super.onPause() }
    override fun onStop() { Log.d(TAG, "onStop"); super.onStop() }
    override fun onDestroy() { Log.d(TAG, "onDestroy"); super.onDestroy() }
}
// endregion lifecycle

// region j2app
/** J2 : on bascule entre 2 écrans avec un simple état (la vraie navigation arrive en J3). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopoteJ2App() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current   // le Context Android, accessible depuis Compose

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { context.startActivity(Intent(context, XmlDemoActivity::class.java)) }) {
                        Icon(painterResource(R.drawable.ic_code), contentDescription = stringResource(R.string.xml_demo))
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(painterResource(R.drawable.ic_restaurant), null) },
                    label = { Text(stringResource(R.string.tab_detail)) },
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(painterResource(R.drawable.ic_add), null) },
                    label = { Text(stringResource(R.string.tab_add)) },
                )
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            0 -> RecipeDetailContent(
                recipe = FakeRecipes.all.first(),
                onAddToShoppingList = { Toast.makeText(context, "Ajouté !", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.padding(innerPadding),
            )
            else -> AddRecipeScreen(
                onSave = { Toast.makeText(context, "Recette « ${it.name} » enregistrée", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
// endregion j2app
