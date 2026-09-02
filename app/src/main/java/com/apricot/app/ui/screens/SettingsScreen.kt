package com.apricot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apricot.app.R
import com.apricot.app.data.mvvm.AppThemeConfig
import com.apricot.app.data.mvvm.SettingsViewModel
import com.apricot.app.data.mvvm.SettingsViewModelFactory
import com.apricot.app.data.mvvm.UserPreferences
import com.apricot.app.data.mvvm.UserPreferencesRepository
import com.apricot.app.ui.components.MultiSelectExposedDropdown
import com.apricot.app.ui.components.SettingSwitchItem
import com.apricot.app.ui.components.ThemeSelectionDropdown
import com.apricot.app.ui.theme.AppTheme

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = remember(context) {
            val repository = UserPreferencesRepository.getInstance(context)
            SettingsViewModelFactory(repository)
        }
    )

    val preferences by viewModel.userPreferences.collectAsState()

    SettingsContent(
        preferences = preferences,
        onUseFoodSpecificMlModelChange = viewModel::updateUseFoodSpecificMlModel,
        onGlutenFreeOnlyChange = viewModel::updateGlutenFreeOnly,
        onVegetarianOnlyChange = viewModel::updateVegetarianOnly,
        onVeganOnlyChange = viewModel::updateVeganOnly,
        onIntolerancesChange = viewModel::updateIntolerances,
        onCuisinesChange = viewModel::updateCuisines,
        onMaxReadyTimeChange = viewModel::updateMaxReadyTime,
        onResultsLimitChange = viewModel::updateResultsLimit,
        onAppColorThemeChange = viewModel::updateAppColorTheme
    )
}

@Composable
fun SettingsContent(
    preferences: UserPreferences,
    onUseFoodSpecificMlModelChange: (Boolean) -> Unit,
    onGlutenFreeOnlyChange: (Boolean) -> Unit,
    onVegetarianOnlyChange: (Boolean) -> Unit,
    onVeganOnlyChange: (Boolean) -> Unit,
    onIntolerancesChange: (Set<String>) -> Unit,
    onCuisinesChange: (Set<String>) -> Unit,
    onMaxReadyTimeChange: (Int?) -> Unit,
    onResultsLimitChange: (Int?) -> Unit,
    onAppColorThemeChange: (AppThemeConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.ML_model_settings_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        SettingSwitchItem(
            title = stringResource(R.string.use_food_specific_ml_model_setting_title),
            summary = stringResource(R.string.use_food_specific_ml_model_setting_summary),
            checked = preferences.useFoodSpecificMlModel,
            onCheckedChange = onUseFoodSpecificMlModelChange
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.default_filters_settings_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        SettingSwitchItem(
            title = stringResource(R.string.gluten_free_only_setting_title),
            summary = stringResource(R.string.gluten_free_only_setting_summary),
            checked = preferences.glutenFreeOnly,
            onCheckedChange = onGlutenFreeOnlyChange
        )

        SettingSwitchItem(
            title = stringResource(R.string.vegetarian_only_setting_title),
            summary = stringResource(R.string.vegetarian_only_setting_summary),
            checked = preferences.vegetarianOnly,
            onCheckedChange = onVegetarianOnlyChange
        )

        SettingSwitchItem(
            title = stringResource(R.string.vegan_only_setting_title),
            summary = stringResource(R.string.vegan_only_setting_summary),
            checked = preferences.veganOnly,
            onCheckedChange = onVeganOnlyChange
        )

        MultiSelectExposedDropdown(
            label = stringResource(R.string.intolerances_input_label),
            options = stringArrayResource(R.array.intolerances_labels).toList(),
            selectedOptions = preferences.intolerances,
            onSelectionChange = onIntolerancesChange,
            modifier = Modifier.fillMaxWidth()
        )

        MultiSelectExposedDropdown(
            label = stringResource(R.string.cuisines_input_label),
            options = stringArrayResource(R.array.cuisines_labels).toList(),
            selectedOptions = preferences.cuisines,
            onSelectionChange = onCuisinesChange,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = preferences.maxReadyTime?.toString() ?: "",
            onValueChange = {
                if (it.isEmpty()) {
                    onMaxReadyTimeChange(null)
                } else {
                    it.toIntOrNull()?.let { time -> onMaxReadyTimeChange(time) }
                }
            },
            label = { Text(stringResource(R.string.max_preparation_time_label)) },
            placeholder = { Text(stringResource(R.string.max_preparation_time_setting_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = preferences.resultsLimit?.toString() ?: "",
            onValueChange = {
                if (it.isEmpty()) {
                    onResultsLimitChange(null)
                } else {
                    it.toIntOrNull()?.let { limit -> onResultsLimitChange(limit) }
                }
            },
            label = { Text(stringResource(R.string.results_limit_label)) },
            placeholder = { Text(stringResource(R.string.results_limit_setting_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.appearence_settings_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        ThemeSelectionDropdown(
            selectedTheme = preferences.appColorTheme,
            onThemeChange = onAppColorThemeChange
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        SettingsContent(
            preferences = UserPreferences(),
            onUseFoodSpecificMlModelChange = {},
            onGlutenFreeOnlyChange = {},
            onVegetarianOnlyChange = {},
            onVeganOnlyChange = {},
            onIntolerancesChange = {},
            onCuisinesChange = {},
            onMaxReadyTimeChange = {},
            onResultsLimitChange = {},
            onAppColorThemeChange = {}
        )
    }
}