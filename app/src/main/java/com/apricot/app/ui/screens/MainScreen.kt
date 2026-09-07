package com.apricot.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.apricot.app.data.model.UserPreferences
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
import com.apricot.app.ui.theme.AppTheme
import kotlin.reflect.typeOf

@Composable
fun MainScreen(userPreferences: UserPreferences = UserPreferences()) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scannerViewModel: ScannerViewModel = viewModel(
        factory = ScannerViewModelFactory(
            context = context.applicationContext,
            preferencesRepository = UserPreferencesRepository.getInstance(context.applicationContext)
        )
    )
    val scannerUiState by scannerViewModel.uiState.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            scannerViewModel.scanImage(bitmap)
        }
    }

    MainScreenContent(
        userPreferences = userPreferences,
        navController = navController,
        scannerUiState = scannerUiState,
        onScanClick = { cameraLauncher.launch(null) },
        onConfirmSearch = { searchParams ->
            val route = SearchResultsRoute(searchParams)
            navController.navigate(route)
            scannerViewModel.onNavigationHandled()
        },
        onRetryScan = {
            scannerViewModel.onNavigationHandled()
            cameraLauncher.launch(null)
        },
        onDismissScannerDialog = {
            scannerViewModel.onNavigationHandled()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    userPreferences: UserPreferences,
    navController: NavHostController,
    scannerUiState: ScannerUiState,
    onScanClick: () -> Unit,
    onConfirmSearch: (SearchParams) -> Unit,
    onRetryScan: () -> Unit,
    onDismissScannerDialog: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    ScannerResultDialog(
        state = scannerUiState,
        onConfirmSearch = onConfirmSearch,
        onRetryScan = onRetryScan,
        onDismiss = onDismissScannerDialog
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
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
                isTabSelected = { routeClass ->
                    val isHome = routeClass == HomeRoute::class && isRouteInCurrentTabFlow(HomeRoute, currentDestination, navController)
                    val isSearch = routeClass == SearchFormRoute::class && isRouteInCurrentTabFlow(SearchFormRoute, currentDestination, navController)
                    val isFav = routeClass == FavouritesRoute::class && isRouteInCurrentTabFlow(FavouritesRoute, currentDestination, navController)
                    val isSettings = routeClass == SettingsRoute::class && isRouteInCurrentTabFlow(SettingsRoute, currentDestination, navController)
                    isHome || isSearch || isFav || isSettings
                },
                onNavigate = { route ->
                    val isCurrentTabFlow = isRouteInCurrentTabFlow(route, currentDestination, navController)

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
                IngredientScannerFAB(
                    onScanClick = onScanClick
                )
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

private fun isRouteInCurrentTabFlow(
    targetRoute: Any,
    currentDestination: NavDestination?,
    navController: NavHostController
): Boolean {
    if (currentDestination == null) return false

    val isHomeInStack = try { navController.getBackStackEntry<HomeRoute>(); true } catch (_: Exception) { false }
    val isSearchInStack = try { navController.getBackStackEntry<SearchFormRoute>(); true } catch (_: Exception) { false }
    val isFavInStack = try { navController.getBackStackEntry<FavouritesRoute>(); true } catch (_: Exception) { false }

    return when (targetRoute) {
        is HomeRoute -> {
            if (currentDestination.hasRoute<HomeRoute>()) return true
            if (currentDestination.hasRoute<SearchResultsRoute>()) {
                return navController.previousBackStackEntry?.destination?.hasRoute<HomeRoute>() == true
            }
            if (currentDestination.hasRoute<RecipeDetailsRoute>()) {
                return isHomeInStack && !isSearchInStack && !isFavInStack
            }
            false
        }
        is SearchFormRoute -> {
            if (currentDestination.hasRoute<SearchFormRoute>()) return true
            if (currentDestination.hasRoute<SearchResultsRoute>()) {
                return navController.previousBackStackEntry?.destination?.hasRoute<SearchFormRoute>() == true
            }
            if (currentDestination.hasRoute<RecipeDetailsRoute>()) {
                return isSearchInStack
            }
            false
        }
        is FavouritesRoute -> {
            if (currentDestination.hasRoute<FavouritesRoute>()) return true
            if (currentDestination.hasRoute<RecipeDetailsRoute>()) {
                return isFavInStack && !isSearchInStack
            }
            false
        }
        is SettingsRoute -> {
            currentDestination.hasRoute<SettingsRoute>()
        }
        else -> false
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppTheme {
        MainScreenContent(
            userPreferences = UserPreferences(),
            navController = rememberNavController(),
            scannerUiState = ScannerUiState.Idle,
            onScanClick = {},
            onConfirmSearch = {},
            onRetryScan = {},
            onDismissScannerDialog = {}
        )
    }
}
