package com.apricot.app.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.apricot.app.R
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.model.SearchParams
import com.apricot.app.data.mvvm.UserPreferences
import com.apricot.app.data.mvvm.UserPreferencesRepository
import com.apricot.app.ui.components.BottomNavigationBar
import com.apricot.app.ui.components.IngredientScannerFAB
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(userPreferences: UserPreferences = UserPreferences()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            // Return to home by clicking on title if it is not already in home
                            if (currentDestination?.hasRoute<HomeRoute>() == false) {
                                navController.navigate(HomeRoute) {
                                    popUpTo<HomeRoute> { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentDestination = currentDestination,
                onNavigate = { route ->
                    val isCurrentTabFlow = when (route) {
                        is FavouritesRoute -> {
                            currentDestination?.hasRoute<FavouritesRoute>() == true ||
                            (currentDestination?.hasRoute<RecipeDetailsRoute>() == true &&
                             navController.previousBackStackEntry?.destination?.hasRoute<FavouritesRoute>() == true)
                        }
                        is SearchFormRoute -> {
                            currentDestination?.hasRoute<SearchFormRoute>() == true ||
                            currentDestination?.hasRoute<SearchResultsRoute>() == true ||
                            (currentDestination?.hasRoute<RecipeDetailsRoute>() == true &&
                             navController.previousBackStackEntry?.destination?.hasRoute<SearchResultsRoute>() == true)
                        }
                        is HomeRoute -> {
                            currentDestination?.hasRoute<HomeRoute>() == true
                        }
                        is SettingsRoute -> {
                            currentDestination?.hasRoute<SettingsRoute>() == true
                        }
                        else -> false
                    }

                    if (isCurrentTabFlow) {
                        navController.popBackStack(
                            route = route,
                            inclusive = false
                        )
                    } else {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            val isHomeScreen = currentDestination?.hasRoute<HomeRoute>() == true
            if (isHomeScreen) {
                val context = LocalContext.current
                IngredientScannerFAB(modifier = Modifier) {
                    // TODO: chiama funzione per scannerizzare e manda richiesta http con quell'ingrediente e i filtri di default, invece se è nella schermata del form aggiunge l'ingrediente alla lista
                    Toast.makeText(context, "Pressed", Toast.LENGTH_SHORT).show()
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier.fillMaxSize()
            ) {
                composable<HomeRoute> {
                    HomeScreen()
                }

                composable<SearchFormRoute> {
                    SearchFormScreen(
                        userPreferences = userPreferences,
                        onSubmit = { params ->
                            val route = SearchResultsRoute(params)
                            navController.navigate(route)
                        }
                    )
                }

                composable<SearchResultsRoute>(
                    typeMap = mapOf(typeOf<SearchParams>() to SearchParamsNavType)
                ) { backStackEntry ->
                    val route: SearchResultsRoute = backStackEntry.toRoute()
                    SearchResultsScreen(
                        searchParams = route.searchParams,
                        onRecipeClick = { recipe ->
                            navController.navigate(
                                route = RecipeDetailsRoute(recipe = recipe)
                            )
                        }
                    )
                }

                composable<RecipeDetailsRoute>(
                    typeMap = mapOf(typeOf<Recipe>() to RecipeNavType)
                ) { backStackEntry ->
                    val route: RecipeDetailsRoute = backStackEntry.toRoute()
                    RecipeDetailsScreen(
                        initialRecipe = route.recipe
                    )
                }

                composable<FavouritesRoute> {
                    FavouritesScreen(
                        onRecipeClick = { recipe ->
                            navController.navigate(
                                route = RecipeDetailsRoute(recipe = recipe)
                            )
                        }
                    )
                }

                composable<SettingsRoute> {
                    SettingsScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppTheme {
        MainScreen()
    }
}