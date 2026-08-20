package com.apricot.app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.apricot.app.R
import com.apricot.app.data.database.AppDatabase
import com.apricot.app.data.mvvm.FavouriteRecipesViewModel
import com.apricot.app.data.mvvm.FavouriteRecipesViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.ui.components.CompactRecipeCard
import com.apricot.app.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

class FavouritesFragment : Fragment() {
    private val viewModel: FavouriteRecipesViewModel by viewModels {
        val api = RetrofitInstance.api
        val dao = AppDatabase.getDatabase(requireContext()).favouriteDao()
        val repository = RecipeRepository(api, dao)
        // Return the Factory with the Repository just created
        FavouriteRecipesViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val onRecipeClick = remember {
                        { recipe: Recipe ->
                            val action = FavouritesFragmentDirections
                                .actionFavouritesFragmentToRecipeDetailsFragment(recipe.id, false)
                            findNavController().navigate(action)
                        }
                    }

                    FavouritesScreen(
                        viewModel = viewModel,
                        onRecipeClick = onRecipeClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    viewModel: FavouriteRecipesViewModel,
    onRecipeClick: (Recipe) -> Unit
) {
    val favouriteRecipes by viewModel.favouriteRecipes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val recipeRemovedText = stringResource(R.string.recipe_removed_from_favourites)
    val undoText = stringResource(R.string.undo)

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favourite_recipes),
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
