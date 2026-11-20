import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // AGP 9 : Kotlin est intégré (plus de plugin org.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)   // routes de navigation @Serializable + JSON
    alias(libs.plugins.ksp)                    // génération de code Room (remplace kapt)
}

android {
    namespace = "fr.formation.popote"
    compileSdk = 37

    defaultConfig {
        applicationId = "fr.formation.popote"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // region signing
    // Les secrets restent HORS du code : keystore.properties (local) ou variables d'env (CI)
    val keystoreFile = rootProject.file("keystore.properties")
    val keystoreProps = Properties().apply { if (keystoreFile.exists()) keystoreFile.inputStream().use(::load) }
    fun secret(key: String, env: String): String? = keystoreProps.getProperty(key) ?: System.getenv(env)

    signingConfigs {
        create("release") {
            secret("storeFile", "POPOTE_STORE_FILE")?.let { storeFile = rootProject.file(it) }
            storePassword = secret("storePassword", "POPOTE_STORE_PASSWORD")
            keyAlias = secret("keyAlias", "POPOTE_KEY_ALIAS")
            keyPassword = secret("keyPassword", "POPOTE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true        // R8 : réduit, optimise et obfusque le code
            isShrinkResources = true      // supprime les ressources inutilisées
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            val release = signingConfigs.getByName("release")
            if (release.storeFile != null) signingConfig = release
        }
    }
    // endregion signing
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true   // BuildConfig.DEBUG   // génère ActivityXmlDemoBinding depuis activity_xml_demo.xml
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // J3 : navigation, ViewModel, coroutines, Koin, images
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // J4 : persistance, réseau, tâches de fond
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.work.runtime.ktx)

    // Tests unitaires (JVM) et instrumentés (appareil)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
