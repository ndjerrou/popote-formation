package fr.formation.popote

import android.app.Application
import fr.formation.popote.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

// region app
/** Point d'entrée du process : démarré AVANT toute Activity (déclaré dans le Manifest). */
class PopoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@PopoteApplication)
            modules(appModules)
        }
    }
}
// endregion app
