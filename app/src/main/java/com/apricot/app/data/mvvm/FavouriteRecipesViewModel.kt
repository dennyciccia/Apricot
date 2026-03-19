package com.apricot.app.data.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apricot.app.data.model.Recipe
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavouriteRecipesViewModel(private val repository: RecipeRepository) : ViewModel() {

    // Transform the repository Flow in a StateFlow for the UI
    val favouriteRecipes: StateFlow<List<Recipe>> = repository.getFavouriteRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

// This class instructs Android how to crete my ViewModel
class FavouriteRecipesViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if Android is requesting the FavouriteRecipesViewModel
        if (modelClass.isAssignableFrom(FavouriteRecipesViewModel::class.java)) {
            return FavouriteRecipesViewModel(repository) as T
        }
        // If it requests another ViewModel throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}