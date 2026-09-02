package com.apricot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apricot.app.R
import com.apricot.app.data.database.AppDatabase
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.mvvm.FavouriteRecipesViewModel
import com.apricot.app.data.mvvm.FavouriteRecipesViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.ui.components.CompactRecipeCard
import com.apricot.app.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FavouritesScreen(
    onRecipeClick: (Recipe) -> Unit
) {
    val context = LocalContext.current
    val viewModel: FavouriteRecipesViewModel = viewModel(
        factory = remember(context) {
            val api = RetrofitInstance.api
            val dao = AppDatabase.getDatabase(context).favouriteDao()
            val repository = RecipeRepository(api, dao)
            FavouriteRecipesViewModelFactory(repository)
        }
    )

    val favouriteRecipes by viewModel.favouriteRecipes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val recipeRemovedText = stringResource(R.string.recipe_removed_from_favourites_message)
    val undoText = stringResource(R.string.undo_recipe_removal_action_label)

    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is FavouriteRecipesViewModel.UiEvent.RecipeRemoved -> {
                    val result = snackbarHostState.showSnackbar(
                        message = recipeRemovedText,
                        actionLabel = undoText,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoRemoveFavorite(event.recipe)
                    }
                }
            }
        }
    }

    val onToggleFavorite = remember(viewModel) {
        { recipe: Recipe -> viewModel.toggleFavorite(recipe) }
    }

    FavouritesContent(
        favouriteRecipes = favouriteRecipes,
        snackbarHostState = snackbarHostState,
        onRecipeClick = onRecipeClick,
        onToggleFavorite = onToggleFavorite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesContent(
    favouriteRecipes: List<Recipe>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onRecipeClick: (Recipe) -> Unit,
    onToggleFavorite: (Recipe) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favourite_screen_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (favouriteRecipes.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_favourites_message),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = favouriteRecipes,
                        key = { it.id },
                        contentType = { "recipe" }
                    ) { recipe ->
                        CompactRecipeCard(
                            title = recipe.title,
                            imageUrl = recipe.imageUrl,
                            prepTime = recipe.readyInMinutes?.let { "$it min" },
                            isFavorite = recipe.isFavourite,
                            onCardClick = { onRecipeClick(recipe) },
                            onFavoriteClick = { onToggleFavorite(recipe) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavouritesScreenPreview() {
    AppTheme {
        FavouritesContent(
            favouriteRecipes = listOf(
                Recipe(
                    id = 1,
                    title = "Pasta Carbonara",
                    imageUrl = "https://example.com/pasta.jpg",
                    readyInMinutes = 20,
                    isFavourite = true
                ),
                Recipe(
                    id = 2,
                    title = "Tiramisù",
                    imageUrl = "https://example.com/tiramisu.jpg",
                    readyInMinutes = 30,
                    isFavourite = true
                )
            ),
            onRecipeClick = {},
            onToggleFavorite = {}
        )
    }
}