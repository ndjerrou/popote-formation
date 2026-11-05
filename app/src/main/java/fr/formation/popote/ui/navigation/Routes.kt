package fr.formation.popote.ui.navigation

import kotlinx.serialization.Serializable

// region routes
// Navigation "type-safe" : chaque destination est une classe sérialisable.
@Serializable data object RecipeListRoute
@Serializable data class RecipeDetailRoute(val recipeId: String)   // argument typé
@Serializable data object ShoppingListRoute
@Serializable data object AddRecipeRoute
// endregion routes
