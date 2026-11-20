// Le fichier du PROJET : on déclare les plugins pour tous les modules, sans les activer (apply false).
plugins {
    alias(libs.plugins.android.application) apply false   // pour :app      (produit l'APK)
    alias(libs.plugins.android.library) apply false       // pour :filmlib  (produit un .aar)
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
