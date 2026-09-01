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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apricot.app.R
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.mvvm.SearchResultsViewModel
import com.apricot.app.ui.components.CompactRecipeCard
import com.apricot.app.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    viewModel: SearchResultsViewModel,
    onRecipeClick: (Recipe) -> Unit
) {
    val recipesList by viewModel.recipesList.collectAsState()

    val onToggleFavorite = remember(viewModel) {
        { recipe: Recipe -> viewModel.toggleFavorite(recipe) }
    }

    SearchResultsContent(
        recipesList = recipesList,
        onRecipeClick = onRecipeClick,
        onToggleFavorite = onToggleFavorite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsContent(
    recipesList: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    onToggleFavorite: (Recipe) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search_results),
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
            if (recipesList.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_results),
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
            onRecipeClick = {},
            onToggleFavorite = {}
        )
    }
}