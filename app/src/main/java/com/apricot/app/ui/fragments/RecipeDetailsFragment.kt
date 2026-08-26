package com.apricot.app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import coil.load
import com.apricot.app.R
import com.apricot.app.data.database.AppDatabase
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.mvvm.RecipeDetailsViewModel
import com.apricot.app.data.mvvm.RecipeDetailsViewModelFactory
import com.apricot.app.data.mvvm.RecipeRepository
import com.apricot.app.data.network.RetrofitInstance
import com.apricot.app.databinding.FragmentRecipeDetailsBinding
import com.apricot.app.ui.components.RecipeInstructionsExtendedFAB
import com.apricot.app.ui.theme.AppTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextAlign
import com.apricot.app.ui.components.AvailableIngredientsCard
import com.apricot.app.ui.icons.avocado_bean
import com.apricot.app.ui.icons.nutrition
import com.apricot.app.ui.icons.temp_preferences_eco
import com.apricot.app.ui.icons.wheat

class RecipeDetailsFragment : Fragment() {
    private val args: RecipeDetailsFragmentArgs by navArgs()
    private var _binding: FragmentRecipeDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeDetailsViewModel by viewModels {
        val api = RetrofitInstance.api
        val dao = AppDatabase.getDatabase(requireContext()).favouriteDao()
        val repository = RecipeRepository(api, dao)
        // Return the Factory with the Repository just created
        RecipeDetailsViewModelFactory(repository)
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
                    // Load and observe recipe data from viewModel
                    LaunchedEffect(args.recipeID, args.dataFromNetwork) {
                        viewModel.loadRecipe(args.recipeID, args.dataFromNetwork)
                    }
                    val recipeState by viewModel.recipeData.collectAsStateWithLifecycle()

                    // Show the recipe only if it is ready
                    recipeState?.let { recipe ->
                        val uriHandler = LocalUriHandler.current
                        RecipeDetailsScreen(
                            recipe = recipe,
                            ingredientsPassed = args.dataFromNetwork,
                            onFavouriteClick = { viewModel.toggleFavourite() },
                            onOpenInstructions = { url -> uriHandler.openUri(url) }
                        )
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // If it is not ready show a loading indicator
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    recipe: Recipe,
    ingredientsPassed: Boolean,
    onFavouriteClick: () -> Unit,
    onOpenInstructions: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            RecipeInstructionsExtendedFAB(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    recipe.sourceUrl?.let { url ->
                        onOpenInstructions(url)
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Hero image
            item {
                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )

                    IconButton(
                        onClick = onFavouriteClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                shape = CircleShape,
                            )
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.icon_favourite_content_description),
                        )
                    }
                }
            }

            // Title, recipe type and cuisine
            item {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val cuisinesList = recipe.cuisines.orEmpty().joinToString(", ")
                    val filteredRecipeTypes = recipe.recipeTypes.orEmpty().filter { type -> type in LocalResources.current.getStringArray(R.array.recipe_types).map { it.lowercase() } }
                    val recipeTypesList = filteredRecipeTypes.joinToString(", ").replace(Regex("\\b\\w")) { matchResult ->
                        matchResult.value.uppercase()
                    }

                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )

                    if (cuisinesList.isNotEmpty())
                        Text(
                            text = cuisinesList,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )

                    if (cuisinesList.isNotEmpty() and recipeTypesList.isNotEmpty())
                        Text(
                            text = stringResource(R.string.dot_spacer),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )

                    if (recipeTypesList.isNotEmpty())
                        Text(
                            text = recipeTypesList,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                }
            }

            // Available ingredients
            if (ingredientsPassed)
                item {
                    // TODO: Quando si fa la chiamata GET per i dettagli della ricetta non vengono forniti gli ingredienti,
                    // TODO: bisogna cambiare la logica in modo che la ricetta venga passata dalla schermata prima,
                    // TODO: nel caso in cui si visualizza una ricetta nei preferiti non serve fare niente perchè gli ingredienti non ci sono in quel caso,
                    // TODO: al massimo si può eseguire questo blocco quando gli ingredienti sono null senza usare args.dataFromNetwork.
                    AvailableIngredientsCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        recipe = recipe,
                    )
                }

            // Chips with information
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    recipe.readyInMinutes?.let { minutes ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(text = stringResource(R.string.preparation_time_label, minutes)) },
                                icon = { Icon(Icons.Default.Timer, contentDescription = null) }
                            )
                        }
                    }

                    if (recipe.glutenFree == true) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.gluten_free_label)) },
                            icon = { Icon(wheat, contentDescription = null) }
                        )
                    }
                    if (recipe.vegan == true) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.vegan_label)) },
                            icon = { Icon(temp_preferences_eco, contentDescription = null) }
                        )
                    } else if (recipe.vegetarian == true) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.vegetarian_label)) },
                            icon = { Icon(avocado_bean, contentDescription = null) }
                        )
                    }
                    if (recipe.sustainable == true) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.sustainable_label)) },
                            icon = { Icon(Icons.Default.Eco, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}