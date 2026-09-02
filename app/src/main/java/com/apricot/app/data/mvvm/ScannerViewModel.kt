package com.apricot.app.data.mvvm

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apricot.app.R
import com.apricot.app.data.ml.PhotoClassifier
import com.apricot.app.data.model.SearchParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ScannerUiState {
    data object Idle : ScannerUiState
    data object Scanning : ScannerUiState
    data class ConfirmIngredient(
        val detectedIngredient: String,
        val searchParams: SearchParams
    ) : ScannerUiState
    data class Error(@param:StringRes val messageRes: Int) : ScannerUiState
}

class ScannerViewModel(
    context: Context,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val applicationContext = context.applicationContext

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState: StateFlow<ScannerUiState> = _uiState

    // Called when pressing on IngredientScannerFAB
    fun scanImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = ScannerUiState.Scanning
            try {
                val preferences = preferencesRepository.userPreferencesFlow.first()

                // Classify image
                val recognizedIngredient = withContext(Dispatchers.Default) {
                    PhotoClassifier(
                        context = applicationContext,
                        useFoodSpecificModel = preferences.useFoodSpecificMlModel
                    ).use { classifier ->
                        classifier.classify(bitmap)
                    }
                }

                // Prepare search parameters with detected ingredient and default search filters
                if (!recognizedIngredient.isNullOrBlank()) {
                    val searchParams = SearchParams(
                        ingredients = listOf(recognizedIngredient),
                        glutenFree = preferences.glutenFreeOnly,
                        vegetarian = preferences.vegetarianOnly,
                        vegan = preferences.veganOnly,
                        cuisines = preferences.cuisines.ifEmpty { null },
                        intolerances = preferences.intolerances.ifEmpty { null },
                        maxPreparationTimeMinutes = preferences.maxReadyTime,
                        resultsLimit = preferences.resultsLimit
                    )
                    _uiState.value = ScannerUiState.ConfirmIngredient(recognizedIngredient, searchParams)
                } else {
                    _uiState.value = ScannerUiState.Error(R.string.no_ingredient_detected_dialog_message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ScannerUiState.Error(R.string.scanning_error_message)
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.value = ScannerUiState.Idle
    }
}

// This class instructs Android how to create my ViewModel
class ScannerViewModelFactory(
    private val context: Context,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
            return ScannerViewModel(context, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
