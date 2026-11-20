// Un module BIBLIOTHÈQUE : pas d'applicationId, pas d'icône, pas d'écran de lancement.
// ./gradlew :filmlib:assembleRelease  ->  filmlib/build/outputs/aar/filmlib-release.aar
plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "fr.formation.filmlib"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true          // l'équipe Film travaille en XML + Fragments (le « vieux » Android)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.fragment.ktx)     // api : l'appli qui utilise la lib voit aussi la classe Fragment
    testImplementation(libs.junit)
}
