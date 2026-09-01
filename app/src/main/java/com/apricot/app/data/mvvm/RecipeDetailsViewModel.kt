package com.apricot.app.data.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apricot.app.data.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    private val repository: RecipeRepository,
    initialRecipe: Recipe
) : ViewModel() {
    // Internal state
    private val _recipeData = MutableStateFlow(initialRecipe)

    // Public state, asStateFlow() hides mutability
    val recipeData: StateFlow<Recipe> = _recipeData.asStateFlow()

    // Called by fragment to toggle favourite status of current diplayed recipe
    fun toggleFavourite() {
        val currentRecipe = _recipeData.value

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

// This class instructs Android how to create my ViewModel
class RecipeDetailsViewModelFactory(
    private val repository: RecipeRepository,
    private val recipe: Recipe,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if Android is requesting the RecipeViewModel
        if (modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java)) {
            return RecipeDetailsViewModel(repository, recipe) as T
        }
        // If it requests another ViewModel throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}