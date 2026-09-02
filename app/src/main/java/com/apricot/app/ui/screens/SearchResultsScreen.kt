package com.apricot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.apricot.app.data.model.SearchParams
import com.apricot.app.data.mvvm.DisplayResultsViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import com.apricot.app.data.mvvm.SearchResultsViewModel
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.ui.components.CompactRecipeCard
import com.apricot.app.ui.theme.AppTheme

@Composable
fun SearchResultsScreen(
    searchParams: SearchParams,
    onRecipeClick: (Recipe) -> Unit
) {
    val context = LocalContext.current
    val viewModel: SearchResultsViewModel = viewModel(
        factory = remember(context) {
            val api = RetrofitInstance.api
            val dao = AppDatabase.getDatabase(context).favouriteDao()
            val repository = RecipeRepository(api, dao)
            DisplayResultsViewModelFactory(repository)
        }
    )

    LaunchedEffect(searchParams) {
        viewModel.loadRecipesIfNeeded(searchParams)
    }

    val recipesList by viewModel.recipesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val onToggleFavorite = remember(viewModel) {
        { recipe: Recipe -> viewModel.toggleFavorite(recipe) }
    }

    SearchResultsContent(
        recipesList = recipesList,
        isLoading = isLoading,
        onRecipeClick = onRecipeClick,
        onToggleFavorite = onToggleFavorite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsContent(
    recipesList: List<Recipe>,
    isLoading: Boolean,
    onRecipeClick: (Recipe) -> Unit,
    onToggleFavorite: (Recipe) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search_results_screen_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                recipesList.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_results_message),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = recipesList,
                            key = { it.id },
                            contentType = { "recipe" }
                        ) { recipe ->
                            CompactRecipeCard(
                                title = recipe.title,
                                imageUrl = recipe.imageUrl,
                                availableIngredients = recipe.usedIngredientCount,
                                totalIngredients = (recipe.usedIngredientCount ?: 0) + (recipe.missedIngredientCount ?: 0),
                                prepTime = recipe.readyInMinutes?.let { stringResource(R.string.preparation_time_label, it) },
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
}

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    AppTheme {
        SearchResultsContent(
            recipesList = listOf(
                Recipe(
                    id = 1,
                    title = "Pasta Carbonara",
                    imageUrl = "https://example.com/pasta.jpg",
                    usedIngredientCount = 3,
                    missedIngredientCount = 2,
                    readyInMinutes = 20,
                    isFavourite = true
                ),
                Recipe(
                    id = 2,
                    title = "Pizza Margherita",
                    imageUrl = "https://example.com/pizza.jpg",
                    usedIngredientCount = 4,
                    missedIngredientCount = 0,
                    readyInMinutes = 15,
                    isFavourite = false
                )
            ),
            isLoading = false,
            onRecipeClick = {},
            onToggleFavorite = {}
        )
    }
}