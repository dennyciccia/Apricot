package com.apricot.app.ui.main

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.preference.PreferenceManager
import com.apricot.app.R
import com.apricot.app.data.database.AppDatabase
import com.apricot.app.data.ml.PhotoClassifier
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.mvvm.SearchResultsViewModel
import com.apricot.app.data.mvvm.DisplayResultsViewModelFactory
import com.apricot.app.data.mvvm.FavouriteRecipesViewModel
import com.apricot.app.data.mvvm.FavouriteRecipesViewModelFactory
import com.apricot.app.data.mvvm.RecipeDetailsViewModel
import com.apricot.app.data.mvvm.RecipeDetailsViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import com.apricot.app.data.mvvm.SettingsViewModel
import com.apricot.app.data.mvvm.SettingsViewModelFactory
import com.apricot.app.data.mvvm.UserPreferences
import com.apricot.app.data.mvvm.UserPreferencesRepository
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.ui.components.BottomNavigationBar
import com.apricot.app.ui.components.IngredientScannerFAB
import com.apricot.app.ui.screens.HomeScreen
import com.apricot.app.ui.screens.SearchResultsScreen
import com.apricot.app.ui.screens.FavouritesScreen
import com.apricot.app.ui.screens.RecipeDetailsScreen
import com.apricot.app.ui.screens.SearchFormScreen
import com.apricot.app.ui.screens.SettingsScreen
import com.apricot.app.data.model.SearchParams
import com.apricot.app.ui.navigation.SearchParamsNavType
import com.apricot.app.ui.navigation.HomeRoute
import com.apricot.app.ui.navigation.SearchFormRoute
import com.apricot.app.ui.navigation.SearchResultsRoute
import com.apricot.app.ui.navigation.FavouritesRoute
import com.apricot.app.ui.navigation.RecipeDetailsRoute
import com.apricot.app.ui.navigation.RecipeNavType
import com.apricot.app.ui.navigation.SettingsRoute
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreen(userPreferences: UserPreferences = UserPreferences()) {
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
                    IngredientScannerFAB(modifier = Modifier) {
                        // TODO: chiama funzione per scannerizzare e manda richiesta http con quell'ingrediente e i filtri di default, invece se è nella schermata del form aggiunge l'ingrediente alla lista
                        Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show()
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
                        val context = LocalContext.current
                        val sharedPreferences = remember(context) { PreferenceManager.getDefaultSharedPreferences(context) }
                        val foodSpecificKey = stringResource(R.string.food_specific_ml_model_key)
                        val useFoodSpecificModel = remember(context, foodSpecificKey) {
                            sharedPreferences.getBoolean(foodSpecificKey, false)
                        }
                        val photoClassifier = remember(context, useFoodSpecificModel) {
                            PhotoClassifier(context, useFoodSpecificModel)
                        }
                        DisposableEffect(photoClassifier) {
                            onDispose { photoClassifier.close() }
                        }

                        var detectedResult by remember { mutableStateOf<String?>(null) }
                        var showNoResultDialog by remember { mutableStateOf(false) }

                        val takePictureLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.TakePicturePreview()
                        ) { bitmap ->
                            if (bitmap != null) {
                                val result = photoClassifier.classify(bitmap)
                                if (result != null) {
                                    detectedResult = result
                                } else {
                                    showNoResultDialog = true
                                }
                            }
                        }

                        SearchFormScreen(
                            userPreferences = userPreferences,
                            detectedIngredientFromCamera = detectedResult,
                            showNoResultDialog = showNoResultDialog,
                            onConfirmDetectedIngredient = { detectedResult = null },
                            onDismissDetectedIngredient = { detectedResult = null },
                            onDismissNoResultDialog = { showNoResultDialog = false },
                            onCameraClick = { takePictureLauncher.launch(null) },
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
                        val context = LocalContext.current
                        val viewModel: SearchResultsViewModel = viewModel(
                            factory = remember(context) {
                                val api = RetrofitInstance.api
                                val dao = AppDatabase.getDatabase(context).favouriteDao()
                                val repository = RecipeRepository(api, dao)
                                DisplayResultsViewModelFactory(repository)
                            }
                        )

                        LaunchedEffect(route) {
                            viewModel.loadRecipesIfNeeded(route.searchParams)
                        }

                        SearchResultsScreen(
                            viewModel = viewModel,
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
                        val context = LocalContext.current
                        val viewModel: RecipeDetailsViewModel = viewModel(
                            key = route.recipe.id.toString(),
                            factory = remember(context) {
                                val api = RetrofitInstance.api
                                val dao = AppDatabase.getDatabase(context).favouriteDao()
                                val repository = RecipeRepository(api, dao)
                                RecipeDetailsViewModelFactory(repository, route.recipe)
                            }
                        )

                        val recipeState by viewModel.recipeData.collectAsStateWithLifecycle()

                        val uriHandler = LocalUriHandler.current
                        RecipeDetailsScreen(
                            recipe = recipeState,
                            onFavouriteClick = { viewModel.toggleFavourite() },
                            onOpenInstructions = { url -> uriHandler.openUri(url) }
                        )
                    }

                    composable<FavouritesRoute> {
                        val context = LocalContext.current
                        val viewModel: FavouriteRecipesViewModel = viewModel(
                            factory = remember(context) {
                                val api = RetrofitInstance.api
                                val dao = AppDatabase.getDatabase(context).favouriteDao()
                                val repository = RecipeRepository(api, dao)
                                FavouriteRecipesViewModelFactory(repository)
                            }
                        )

                        FavouritesScreen(
                            viewModel = viewModel,
                            onRecipeClick = { recipe ->
                                navController.navigate(
                                    route = RecipeDetailsRoute(recipe = recipe)
                                )
                            }
                        )
                    }

                    composable<SettingsRoute> {
                        val context = LocalContext.current
                        val viewModel: SettingsViewModel = viewModel(
                            factory = remember(context) {
                                val repository = UserPreferencesRepository.getInstance(context)
                                SettingsViewModelFactory(repository)
                            }
                        )

                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
