import java.util.Properties

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.7")
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.legacy.kapt)
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.apricot.app"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    defaultConfig {
        applicationId = "com.apricot.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val apiKey = properties.getProperty("SPOONACULAR_API_KEY")
        buildConfigField("String", "SPOONACULAR_API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // --- JETPACK COMPOSE (tramite Compose BOM) ---
    val composeBom = platform("androidx.compose:compose-bom:2026.06.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Librerie base UI e Fondamenta
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.googlefonts)

    // Material Design 3 (Material You)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coilCompose)

    // Integrazione con Activity
    implementation(libs.androidx.activity.compose)

    // Anteprima UI in Android Studio
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation (per passare da una schermata all'altra in Compose)
    implementation(libs.androidx.navigation.compose)

    // Integrazione ViewModel con Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.recyclerview)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("com.google.mediapipe:tasks-vision:latest.release")
    kapt("androidx.room:room-compiler:2.8.4")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}