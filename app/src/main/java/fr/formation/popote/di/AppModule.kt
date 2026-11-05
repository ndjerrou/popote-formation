package fr.formation.popote.di

import fr.formation.popote.data.fake.InMemoryRecipeRepository
import fr.formation.popote.data.fake.InMemoryShoppingRepository
import fr.formation.popote.domain.RecipeRepository
import fr.formation.popote.domain.ShoppingRepository
import fr.formation.popote.ui.detail.RecipeDetailViewModel
import fr.formation.popote.ui.form.AddRecipeViewModel
import fr.formation.popote.ui.list.RecipeListViewModel
import fr.formation.popote.ui.shopping.ShoppingListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// region koin
val dataModule = module {
    // single = une seule instance pour toute l'app ; on expose l'INTERFACE
    single<RecipeRepository> { InMemoryRecipeRepository() }
    single<ShoppingRepository> { InMemoryShoppingRepository() }
}

val uiModule = module {
    // viewModelOf : Koin résout les paramètres du constructeur (repos, SavedStateHandle...)
    viewModelOf(::RecipeListViewModel)
    viewModelOf(::RecipeDetailViewModel)
    viewModelOf(::ShoppingListViewModel)
    viewModelOf(::AddRecipeViewModel)
}

val appModules = listOf(dataModule, uiModule)
// endregion koin
