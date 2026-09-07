package com.apricot.app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.apricot.app.data.model.UserPreferences
import com.apricot.app.data.mvvm.UserPreferencesRepository
import com.apricot.app.ui.screens.MainScreen
import com.apricot.app.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = UserPreferencesRepository.getInstance(applicationContext)

        setContent {
            val preferences by repository.userPreferencesFlow.collectAsState(initial = UserPreferences())

            AppTheme(themeConfig = preferences.appColorTheme) {
                MainScreen(userPreferences = preferences)
            }
        }
    }
}
