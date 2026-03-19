package com.apricot.app.data.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apricot.app.data.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(private val repository: RecipeRepository) : ViewModel() {
    // Internal state is intialized as null until data arrive
    private val _recipeData = MutableStateFlow<Recipe?>(null)

    // Public state, asStateFlow() hides mutability
    val recipeData: StateFlow<Recipe?> = _recipeData.asStateFlow()

    // Called by fragment to load data
    fun loadRecipe(recipeID: Int, fromNetwork: Boolean) {
        // viewModelScope launches the coroutine linked to ViewModel lifecycle
        // If ViewModel is destroyed, the call stops automatically
        viewModelScope.launch {
            try {
                // Ask data to repository and update the state, the fragment will immediately notice
                val recipe = repository.getRecipeDetails(recipeID, fromNetwork)
                _recipeData.value = recipe

            } catch (e: Exception) {
                // API or DB query failed
                e.printStackTrace()
                _recipeData.value = null
            }
        }
    }

    // Called by fragment to toggle favourite status of current diplayed recipe
    fun toggleFavourite() {
        // Get current recipe, if null do nothing
        val currentRecipe = _recipeData.value ?: return

        viewModelScope.launch {
            try {
                if (currentRecipe.isFavourite) {
                    // If already favourite remove from DB
                    repository.removeFromFavorites(currentRecipe)
                    _recipeData.value = currentRecipe.copy(isFavourite = false)
                } else {
                    // If not already favourite save in DB
                    repository.saveAsFavorite(currentRecipe)
                    _recipeData.value = currentRecipe.copy(isFavourite = true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

// This class instructs Android how to crete my ViewModel
class RecipeDetailsViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if Android is requesting the RecipeViewModel
        if (modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java)) {
            return RecipeDetailsViewModel(repository) as T
        }
        // If it requests another ViewModel throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}