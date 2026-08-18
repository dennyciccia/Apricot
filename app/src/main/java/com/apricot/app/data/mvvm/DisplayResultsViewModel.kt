package com.apricot.app.data.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apricot.app.data.model.Recipe
import com.apricot.app.ui.fragments.DisplayResultsFragmentArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DisplayResultsViewModel (private val repository: RecipeRepository) : ViewModel() {
    private val _recipesList = MutableStateFlow<List<Recipe>>(emptyList())
    val recipesList: StateFlow<List<Recipe>> = _recipesList
    private var isLoaded = false

    // Called by fragment to load data
    fun loadRecipesIfNeeded(searchArgs: DisplayResultsFragmentArgs) {
        if(isLoaded) return

        viewModelScope.launch {
            try {
                val recipes = repository.findRecipes(searchArgs)
                _recipesList.value = recipes
                isLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
                _recipesList.value = emptyList()
                isLoaded = false
            }
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                if (recipe.isFavourite) {
                    repository.removeFromFavorites(recipe)
                } else {
                    repository.saveAsFavorite(recipe)
                }
                // Update local list state in order to show the change on the UI
                _recipesList.value = _recipesList.value.map {
                    if (it.id == recipe.id) it.copy(isFavourite = !recipe.isFavourite) else it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

// This class instructs Android how to create my ViewModel
class DisplayResultsViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if Android is requesting the DisplayResultsViewModel
        if (modelClass.isAssignableFrom(DisplayResultsViewModel::class.java)) {
            return DisplayResultsViewModel(repository) as T
        }
        // If it requests another ViewModel throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}