package com.apricot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apricot.app.R
import com.apricot.app.data.mvvm.UserPreferences
import com.apricot.app.ui.components.DiscreteSlider
import com.apricot.app.ui.components.IngredientsInput
import com.apricot.app.ui.components.MultiSelectExposedDropdown
import com.apricot.app.ui.icons.avocado_bean
import com.apricot.app.ui.icons.temp_preferences_eco
import com.apricot.app.ui.icons.wheat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFormScreen(
    userPreferences: UserPreferences = UserPreferences(),
    detectedIngredientFromCamera: String?,
    showNoResultDialog: Boolean,
    onConfirmDetectedIngredient: () -> Unit,
    onDismissDetectedIngredient: () -> Unit,
    onDismissNoResultDialog: () -> Unit,
    onCameraClick: () -> Unit,
    onSubmit: (List<String>, String, Set<String>, Boolean, Boolean, Boolean, Set<String>, Set<String>, Int, Int) -> Unit
) {
    // Initialize input fields values on first composition
    var ingredients by remember { mutableStateOf(emptyList<String>()) }
    var query by remember { mutableStateOf("") }
    var selectedTypes by remember { mutableStateOf(emptySet<String>()) }
    var isGlutenFree by remember { mutableStateOf(userPreferences.glutenFreeOnly) }
    var isVegan by remember { mutableStateOf(userPreferences.veganOnly) }
    var isVegetarian by remember { mutableStateOf(userPreferences.vegetarianOnly) }
    var selectedIntolerances by remember { mutableStateOf(userPreferences.intolerances) }
    var selectedCuisines by remember { mutableStateOf(userPreferences.cuisines) }
    var maxReadyTime by remember { mutableIntStateOf(userPreferences.maxReadyTime ?: 60) }
    var resultsLimit by remember { mutableIntStateOf(userPreferences.resultsLimit ?: 20) }

    // Update input field values if preferences changes after composition
    LaunchedEffect(userPreferences) {
        isGlutenFree = userPreferences.glutenFreeOnly
        isVegan = userPreferences.veganOnly
        isVegetarian = userPreferences.vegetarianOnly
        selectedIntolerances = userPreferences.intolerances
        selectedCuisines = userPreferences.cuisines
        maxReadyTime = userPreferences.maxReadyTime ?: 60
        resultsLimit = userPreferences.resultsLimit ?: 20
    }

    // Called by reset button
    val resetFields = {
        ingredients = emptyList()
        query = ""
        selectedTypes = emptySet()
        isGlutenFree = false
        isVegan = false
        isVegetarian = false
        selectedIntolerances = emptySet()
        selectedCuisines = emptySet()
        maxReadyTime = 60
        resultsLimit = 20
    }

    // Alert dialog with detected ingredient from camera
    if (detectedIngredientFromCamera != null) {
        AlertDialog(
            onDismissRequest = onDismissDetectedIngredient,
            title = { Text(stringResource(R.string.detected_ingredient_dialog_title)) },
            text = { Text(stringResource(R.string.found_ingredient_message, detectedIngredientFromCamera)) },
            confirmButton = {
                TextButton(onClick = {
                    if (!ingredients.contains(detectedIngredientFromCamera)) {
                        ingredients = ingredients + detectedIngredientFromCamera
                    }
                    onConfirmDetectedIngredient()
                }) {
                    Text(stringResource(R.string.add_detected_ingredient_confirm_label))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDetectedIngredient) {
                    Text(stringResource(R.string.add_detected_ingredient_deny_label))
                }
            }
        )
    }

    // Alert dialog with no ingredient found message
    if (showNoResultDialog) {
        AlertDialog(
            onDismissRequest = onDismissNoResultDialog,
            title = { Text(stringResource(R.string.detected_ingredient_dialog_title)) },
            text = { Text(stringResource(R.string.no_ingredient_detected_dialog_message)) },
            confirmButton = {
                TextButton(onClick = onDismissNoResultDialog) {
                    Text(stringResource(R.string.no_ingredient_detected_dialog_confirm_label))
                }
            }
        )
    }

    // Form interface
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search_screen_title),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = resetFields,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.button_reset_search_form))
                    }
                    Button(
                        onClick = {
                            onSubmit(
                                ingredients,
                                query,
                                selectedTypes,
                                isGlutenFree,
                                isVegan,
                                isVegetarian,
                                selectedIntolerances,
                                selectedCuisines,
                                maxReadyTime,
                                resultsLimit
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.button_submit_search_form))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top padding adjustment
                    Spacer(modifier = Modifier.height(0.dp))

                    // Ingredients input section
                    Text(
                        text = stringResource(R.string.ingrediets_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    IngredientsInput(
                        ingredients = ingredients,
                        onIngredientsChange = { ingredients = it },
                        onCameraClick = onCameraClick
                    )

                    HorizontalDivider()

                    // Query and recipe type section
                    Text(
                        text = stringResource(R.string.recipe_type_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.label_query_input)) },
                        placeholder = { Text(stringResource(R.string.placeholder_query_input)) },
                        singleLine = true,
                    )

                    MultiSelectExposedDropdown(
                        label = stringResource(R.string.recipe_type_input_label),
                        options = stringArrayResource(R.array.recipe_types).toList(),
                        selectedOptions = selectedTypes,
                        onSelectionChange = { selectedTypes = it }
                    )

                    HorizontalDivider()

                    // Diets and intolerances section
                    Text(
                        text = stringResource(R.string.diet_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isGlutenFree,
                            onClick = { isGlutenFree = !isGlutenFree },
                            label = { Text(stringResource(R.string.gluten_free_label)) },
                            leadingIcon = {
                                if (isGlutenFree) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                } else {
                                    Icon(
                                        imageVector = wheat,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            }
                        )
                        FilterChip(
                            selected = isVegan,
                            onClick = {
                                isVegan = !isVegan
                                if (isVegan) isVegetarian = false
                            },
                            label = { Text(stringResource(R.string.vegan_label)) },
                            leadingIcon = {
                                if (isVegan) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                } else {
                                    Icon(
                                        imageVector = temp_preferences_eco,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            }
                        )
                        FilterChip(
                            selected = isVegetarian,
                            onClick = {
                                isVegetarian = !isVegetarian
                                if (isVegetarian) isVegan = false
                            },
                            label = { Text(stringResource(R.string.vegetarian_label)) },
                            leadingIcon = {
                                if (isVegetarian) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                } else {
                                    Icon(
                                        imageVector = avocado_bean,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            }
                        )
                    }

                    MultiSelectExposedDropdown(
                        label = stringResource(R.string.intolerances),
                        options = stringArrayResource(R.array.intolerances_labels).toList(),
                        selectedOptions = selectedIntolerances,
                        onSelectionChange = { selectedIntolerances = it }
                    )

                    HorizontalDivider()

                    // Cuisine, max, preparation time and results limit section
                    Text(
                        text = stringResource(R.string.preferences_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    MultiSelectExposedDropdown(
                        label = stringResource(R.string.cuisines),
                        options = stringArrayResource(R.array.cuisines_labels).toList(),
                        selectedOptions = selectedCuisines,
                        onSelectionChange = { selectedCuisines = it }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.max_preparation_time_label),
                            style = MaterialTheme.typography.labelLarge
                        )
                        DiscreteSlider(
                            value = maxReadyTime,
                            onValueChange = { maxReadyTime = it },
                            min = 5,
                            max = 120,
                            textFieldSuffix = { Text("min") }
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.results_limit_label),
                            style = MaterialTheme.typography.labelLarge
                        )
                        DiscreteSlider(
                            value = resultsLimit,
                            onValueChange = { resultsLimit = it },
                            min = 1,
                            max = 50
                        )
                    }

                    // Bottom padding adjustment
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}