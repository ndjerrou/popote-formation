package fr.formation.popote.ui.navigation

import android.content.Intent
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.formation.popote.R
import fr.formation.popote.legacy.XmlDemoActivity
import fr.formation.popote.ui.detail.RecipeDetailRoute
import fr.formation.popote.ui.form.AddRecipeRoute
import fr.formation.popote.ui.list.RecipeListRoute
import fr.formation.popote.ui.shopping.ShoppingListRoute
import kotlin.reflect.KClass

private data class Tab(val route: Any, val routeClass: KClass<*>, val label: String, @DrawableRes val icon: Int)

private val tabs = listOf(
    Tab(RecipeListRoute, RecipeListRoute::class, "Recettes", R.drawable.ic_restaurant),
    Tab(ShoppingListRoute, ShoppingListRoute::class, "Courses", R.drawable.ic_cart),
    Tab(AddRecipeRoute, AddRecipeRoute::class, "Ajouter", R.drawable.ic_add),
)

// region navhost
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopoteNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val context = LocalContext.current
    val isTopLevel = tabs.any { tab -> currentDestination?.hasRoute(tab.routeClass) == true }

    Scaffold(
        topBar = {
            if (isTopLevel) TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { context.startActivity(Intent(context, XmlDemoActivity::class.java)) }) {
                        Icon(painterResource(R.drawable.ic_code), stringResource(R.string.xml_demo))
                    }
                },
            )
        },
        bottomBar = {
            if (isTopLevel) NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(tab.routeClass) == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painterResource(tab.icon), null) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(navController, startDestination = RecipeListRoute, modifier = Modifier.padding(padding)) {
            composable<RecipeListRoute> {
                RecipeListRoute(onRecipeClick = { id -> navController.navigate(RecipeDetailRoute(id)) })
            }
            composable<RecipeDetailRoute> {
                // l'argument recipeId est lu par le ViewModel via SavedStateHandle.toRoute()
                RecipeDetailRoute(onBack = { navController.popBackStack() })
            }
            composable<ShoppingListRoute> { ShoppingListRoute() }
            composable<AddRecipeRoute> {
                AddRecipeRoute(onSaved = { navController.navigate(ShoppingListRoute) { launchSingleTop = true } })
            }
        }
    }
}
// endregion navhost
