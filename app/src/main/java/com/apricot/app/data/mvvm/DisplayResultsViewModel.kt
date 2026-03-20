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
    private val _recipesList = MutableStateFlow<List<RecipeInComplexSearch>>(emptyList())
    val recipesList: StateFlow<List<RecipeInComplexSearch>> = _recipesList
    private var isLoaded = false

    // Called by fragment to load data
    fun loadRecipesIfNeeded(searchArgs: DisplayResultsFragmentArgs) {
        if(isLoaded) return

        // viewModelScope launches the coroutine linked to ViewModel lifecycle
        // If ViewModel is destroyed, the call stops automatically
        viewModelScope.launch {
            try {
                // Ask data to repository and update the state, the fragment will immediately notice
                _recipesList.value = repository.findRecipes(searchArgs)
                isLoaded = true
            } catch (e: Exception) {
                // API or DB query failed
                e.printStackTrace()
                _recipesList.value = emptyList()
                isLoaded = false
            }
        }
    }
}

// This class instructs Android how to crete my ViewModel
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