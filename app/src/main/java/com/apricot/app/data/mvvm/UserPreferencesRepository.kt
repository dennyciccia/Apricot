package com.apricot.app.data.mvvm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val FOOD_SPECIFIC_ML_MODEL = booleanPreferencesKey("food_specific_ml_model")
        val GLUTEN_FREE_ONLY = booleanPreferencesKey("gluten_free_only")
        val VEGETARIAN_ONLY = booleanPreferencesKey("vegetarian_only")
        val VEGAN_ONLY = booleanPreferencesKey("vegan_only")
        val CUISINES = stringSetPreferencesKey("cuisines")
        val INTOLERANCES = stringSetPreferencesKey("intolerances")
        val MAX_READY_TIME = stringPreferencesKey("max_ready_time")
        val RESULTS_LIMIT = stringPreferencesKey("results_limit")
        val APP_COLOR_THEME = stringPreferencesKey("app_color_theme")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapUserPreferences(preferences)
        }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            useFoodSpecificMlModel = preferences[PreferencesKeys.FOOD_SPECIFIC_ML_MODEL] ?: false,
            glutenFreeOnly = preferences[PreferencesKeys.GLUTEN_FREE_ONLY] ?: false,
            vegetarianOnly = preferences[PreferencesKeys.VEGETARIAN_ONLY] ?: false,
            veganOnly = preferences[PreferencesKeys.VEGAN_ONLY] ?: false,
            cuisines = preferences[PreferencesKeys.CUISINES] ?: emptySet(),
            intolerances = preferences[PreferencesKeys.INTOLERANCES] ?: emptySet(),
            maxReadyTime = preferences[PreferencesKeys.MAX_READY_TIME]?.toIntOrNull(),
            resultsLimit = preferences[PreferencesKeys.RESULTS_LIMIT]?.toIntOrNull(),
            appColorTheme = AppThemeConfig.valueOf(
                preferences[PreferencesKeys.APP_COLOR_THEME] ?: AppThemeConfig.SYSTEM.name
            )
        )
    }

    suspend fun updateUseFoodSpecificMlModel(useFoodSpecificMlModel: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FOOD_SPECIFIC_ML_MODEL] = useFoodSpecificMlModel
        }
    }

    suspend fun updateGlutenFreeOnly(glutenFreeOnly: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GLUTEN_FREE_ONLY] = glutenFreeOnly
        }
    }

    suspend fun updateVegetarianOnly(vegetarianOnly: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VEGETARIAN_ONLY] = vegetarianOnly
            if (vegetarianOnly) {
                preferences[PreferencesKeys.VEGAN_ONLY] = false
            }
        }
    }

    suspend fun updateVeganOnly(veganOnly: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VEGAN_ONLY] = veganOnly
            if (veganOnly) {
                preferences[PreferencesKeys.VEGETARIAN_ONLY] = false
            }
        }
    }

    suspend fun updateCuisines(cuisines: Set<String>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CUISINES] = cuisines
        }
    }

    suspend fun updateIntolerances(intolerances: Set<String>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.INTOLERANCES] = intolerances
        }
    }

    suspend fun updateMaxReadyTime(maxReadyTime: Int?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MAX_READY_TIME] = maxReadyTime?.toString() ?: ""
        }
    }

    suspend fun updateResultsLimit(resultsLimit: Int?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.RESULTS_LIMIT] = resultsLimit?.toString() ?: ""
        }
    }

    suspend fun updateAppColorTheme(appColorTheme: AppThemeConfig) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_COLOR_THEME] = appColorTheme.name
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferencesRepository(context.applicationContext.dataStore).also { INSTANCE = it }
            }
        }
    }
}
