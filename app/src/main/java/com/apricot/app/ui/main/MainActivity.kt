package com.apricot.app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.apricot.app.R
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.model.SearchParams
import com.apricot.app.data.mvvm.ScannerUiState
import com.apricot.app.data.mvvm.ScannerViewModel
import com.apricot.app.data.mvvm.ScannerViewModelFactory
import com.apricot.app.data.mvvm.UserPreferences
import com.apricot.app.data.mvvm.UserPreferencesRepository
import com.apricot.app.ui.components.BottomNavigationBar
import com.apricot.app.ui.components.IngredientScannerFAB
import com.apricot.app.ui.components.ScannerResultDialog
import com.apricot.app.ui.navigation.FavouritesRoute
import com.apricot.app.ui.navigation.HomeRoute
import com.apricot.app.ui.navigation.RecipeDetailsRoute
import com.apricot.app.ui.navigation.RecipeNavType
import com.apricot.app.ui.navigation.SearchFormRoute
import com.apricot.app.ui.navigation.SearchParamsNavType
import com.apricot.app.ui.navigation.SearchResultsRoute
import com.apricot.app.ui.navigation.SettingsRoute
import com.apricot.app.ui.screens.FavouritesScreen
import com.apricot.app.ui.screens.HomeScreen
import com.apricot.app.ui.screens.MainScreen
import com.apricot.app.ui.screens.RecipeDetailsScreen
import com.apricot.app.ui.screens.SearchFormScreen
import com.apricot.app.ui.screens.SearchResultsScreen
import com.apricot.app.ui.screens.SettingsScreen
import com.apricot.app.ui.theme.AppTheme
import kotlin.reflect.typeOf

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
