package fr.formation.popote.di

import androidx.room.Room
import fr.formation.popote.BuildConfig
import fr.formation.popote.data.OfflineFirstRecipeRepository
import fr.formation.popote.data.RoomShoppingRepository
import fr.formation.popote.data.local.PopoteDatabase
import fr.formation.popote.data.prefs.SettingsRepository
import fr.formation.popote.data.remote.MealDbApi
import fr.formation.popote.data.remote.MealMapper
import fr.formation.popote.domain.RecipeRepository
import fr.formation.popote.domain.ReminderSettings
import fr.formation.popote.domain.ShoppingRepository
import fr.formation.popote.ui.detail.RecipeDetailViewModel
import fr.formation.popote.ui.form.AddRecipeViewModel
import fr.formation.popote.ui.list.RecipeListViewModel
import fr.formation.popote.ui.shopping.ShoppingListViewModel
import fr.formation.popote.work.ReminderManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

// region network
val networkModule = module {
    single { Json { ignoreUnknownKeys = true } }   // tolérant aux champs inconnus
    single {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }
    single<MealDbApi> {
        Retrofit.Builder()
            .baseUrl(MealDbApi.BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MealDbApi::class.java)
    }
    singleOf(::MealMapper)
}
// endregion network

// region database
val databaseModule = module {
    single { Room.databaseBuilder(androidContext(), PopoteDatabase::class.java, "popote.db").build() }
    single { get<PopoteDatabase>().favoriteDao() }
    single { get<PopoteDatabase>().shoppingDao() }
    single { get<PopoteDatabase>().recipeCacheDao() }
}
// endregion database

// region koin
val dataModule = module {
    // J4 : on remplace les implémentations en mémoire SANS toucher à l'UI
    singleOf(::OfflineFirstRecipeRepository) bind RecipeRepository::class
    singleOf(::RoomShoppingRepository) bind ShoppingRepository::class
    singleOf(::SettingsRepository)
    singleOf(::ReminderManager) bind ReminderSettings::class
}

val uiModule = module {
    viewModelOf(::RecipeListViewModel)
    viewModelOf(::RecipeDetailViewModel)
    viewModelOf(::ShoppingListViewModel)
    viewModelOf(::AddRecipeViewModel)
}

val appModules = listOf(networkModule, databaseModule, dataModule, uiModule)
// endregion koin
