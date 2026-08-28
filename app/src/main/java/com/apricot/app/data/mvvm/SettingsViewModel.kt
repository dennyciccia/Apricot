package com.apricot.app.data.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = repository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateUseFoodSpecificMlModel(value: Boolean) {
        viewModelScope.launch { repository.updateUseFoodSpecificMlModel(value) }
    }

    fun updateGlutenFreeOnly(value: Boolean) {
        viewModelScope.launch { repository.updateGlutenFreeOnly(value) }
    }

    fun updateVegetarianOnly(value: Boolean) {
        viewModelScope.launch { repository.updateVegetarianOnly(value) }
    }

    fun updateVeganOnly(value: Boolean) {
        viewModelScope.launch { repository.updateVeganOnly(value) }
    }

    fun updateCuisines(value: Set<String>) {
        viewModelScope.launch { repository.updateCuisines(value) }
    }

    fun updateIntolerances(value: Set<String>) {
        viewModelScope.launch { repository.updateIntolerances(value) }
    }

    fun updateMaxReadyTime(value: Int?) {
        viewModelScope.launch { repository.updateMaxReadyTime(value) }
    }

    fun updateResultsLimit(value: Int?) {
        viewModelScope.launch { repository.updateResultsLimit(value) }
    }

    fun updateAppColorTheme(value: AppThemeConfig) {
        viewModelScope.launch { repository.updateAppColorTheme(value) }
    }
}

// This class instructs Android how to create my ViewModel
class SettingsViewModelFactory(
    private val repository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
