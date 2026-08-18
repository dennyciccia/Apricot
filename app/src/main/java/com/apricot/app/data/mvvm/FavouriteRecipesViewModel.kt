package com.apricot.app.data.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apricot.app.data.model.Recipe
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavouriteRecipesViewModel(private val repository: RecipeRepository) : ViewModel() {

    // Transform the repository Flow in a StateFlow for the UI
    val favouriteRecipes: StateFlow<List<Recipe>> = repository.getFavouriteRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                if (recipe.isFavourite) {
                    repository.removeFromFavorites(recipe)
                    _events.emit(UiEvent.RecipeRemoved(recipe))
                } else {
                    repository.saveAsFavorite(recipe)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun undoRemoveFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.saveAsFavorite(recipe)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    sealed class UiEvent {
        data class RecipeRemoved(val recipe: Recipe) : UiEvent()
    }
}

// This class instructs Android how to create my ViewModel
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