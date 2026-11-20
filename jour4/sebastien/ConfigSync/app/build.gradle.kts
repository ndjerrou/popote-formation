import java.util.Properties

// Les clés de l'API ne sont JAMAIS écrites dans le code ni envoyées sur git :
// elles sont lues dans local.properties (ignoré par git), puis copiées dans BuildConfig.
val secrets = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
fun secret(key: String, demo: String) = "\"" + (secrets.getProperty(key) ?: demo) + "\""

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "fr.formation.configsync"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "fr.formation.configsync"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Valeurs de démo = le faux serveur du dossier server/ (lancé sur ton Mac, vu depuis l'émulateur en 10.0.2.2)
        buildConfigField("String", "API_BASE_URL", secret("api.baseUrl", "http://10.0.2.2:8080"))
        buildConfigField("String", "API_PUBLIC_KEY", secret("api.publicKey", "pub_demo_123"))
        buildConfigField("String", "API_PRIVATE_KEY", secret("api.privateKey", "priv_demo_456"))
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.documentfile)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}