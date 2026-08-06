// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.legacy.kapt) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.7" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.10" apply false
}