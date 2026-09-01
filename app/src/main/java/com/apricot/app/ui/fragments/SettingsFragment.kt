package com.apricot.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.apricot.app.R
import com.apricot.app.data.mvvm.SettingsViewModel
import com.apricot.app.data.mvvm.SettingsViewModelFactory
import com.apricot.app.data.mvvm.UserPreferencesRepository
import com.apricot.app.ui.components.MultiSelectExposedDropdown
import com.apricot.app.ui.components.SettingSwitchItem
import com.apricot.app.ui.components.ThemeSelectionDropdown
import com.apricot.app.ui.theme.AppTheme

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = UserPreferencesRepository.getInstance(requireContext())
        val factory = SettingsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                val preferences by viewModel.userPreferences.collectAsState()
                AppTheme(themeConfig = preferences.appColorTheme) {
                    SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val preferences by viewModel.userPreferences.collectAsState()

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
            title = stringResource(R.string.food_specific_ml_model),
            summary = stringResource(R.string.food_specific_ml_model_summary),
            checked = preferences.useFoodSpecificMlModel,
            onCheckedChange = { viewModel.updateUseFoodSpecificMlModel(it) }
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.default_filters_settings_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        SettingSwitchItem(
            title = stringResource(R.string.gluten_free_only),
            summary = stringResource(R.string.gluten_free_only_summary),
            checked = preferences.glutenFreeOnly,
            onCheckedChange = { viewModel.updateGlutenFreeOnly(it) }
        )

        SettingSwitchItem(
            title = stringResource(R.string.vegetarian_only),
            summary = stringResource(R.string.vegetarian_only_summary),
            checked = preferences.vegetarianOnly,
            onCheckedChange = { viewModel.updateVegetarianOnly(it) }
        )

        SettingSwitchItem(
            title = stringResource(R.string.vegan_only),
            summary = stringResource(R.string.vegan_only_summary),
            checked = preferences.veganOnly,
            onCheckedChange = { viewModel.updateVeganOnly(it) }
        )

        MultiSelectExposedDropdown(
            label = stringResource(R.string.cuisines),
            options = stringArrayResource(R.array.cuisines_labels).toList(),
            selectedOptions = preferences.cuisines,
            onSelectionChange = { viewModel.updateCuisines(it) },
            modifier = Modifier.fillMaxWidth()
        )

        MultiSelectExposedDropdown(
            label = stringResource(R.string.intolerances),
            options = stringArrayResource(R.array.intolerances_labels).toList(),
            selectedOptions = preferences.intolerances,
            onSelectionChange = { viewModel.updateIntolerances(it) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = preferences.maxReadyTime?.toString() ?: "",
            onValueChange = {
                if (it.isEmpty()) {
                    viewModel.updateMaxReadyTime(null)
                } else {
                    it.toIntOrNull()?.let { time -> viewModel.updateMaxReadyTime(time) }
                }
            },
            label = { Text(stringResource(R.string.max_preparation_time_label)) },
            placeholder = { Text(stringResource(R.string.hint_edit_text_max_ready_time)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = preferences.resultsLimit?.toString() ?: "",
            onValueChange = {
                if (it.isEmpty()) {
                    viewModel.updateResultsLimit(null)
                } else {
                    it.toIntOrNull()?.let { limit -> viewModel.updateResultsLimit(limit) }
                }
            },
            label = { Text(stringResource(R.string.results_limit_label)) },
            placeholder = { Text(stringResource(R.string.hint_edit_text_results_limit)) },
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
            onThemeChange = { viewModel.updateAppColorTheme(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
