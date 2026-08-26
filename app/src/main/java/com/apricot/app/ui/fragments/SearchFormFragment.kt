package com.apricot.app.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.apricot.app.R
import com.apricot.app.data.ml.PhotoClassifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apricot.app.ui.components.DiscreteSlider
import com.apricot.app.ui.components.IngredientsInput
import com.apricot.app.ui.components.MultiSelectExposedDropdown
import com.apricot.app.ui.icons.avocado_bean
import com.apricot.app.ui.icons.temp_preferences_eco
import com.apricot.app.ui.icons.wheat
import com.apricot.app.ui.theme.AppTheme

class SearchFormFragment : Fragment() {
    private lateinit var photoClassifier: PhotoClassifier
    private val detectedResult = mutableStateOf<String?>(null)
    private var showNoResultDialog = mutableStateOf(false)

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val result = photoClassifier.classify(bitmap)
            if (result != null) {
                detectedResult.value = result
            } else {
                showNoResultDialog.value = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val detectedIngredient by detectedResult
                    val showNoResult by showNoResultDialog
                    
                    SearchFormScreen(
                        detectedIngredientFromCamera = detectedIngredient,
                        showNoResultDialog = showNoResult,
                        onConfirmDetectedIngredient = {
                            detectedResult.value = null
                        },
                        onDismissDetectedIngredient = {
                            detectedResult.value = null
                        },
                        onDismissNoResultDialog = {
                            showNoResultDialog.value = false
                        },
                        onCameraClick = {
                            takePictureLauncher.launch(null)
                        },
                        onSubmit = { ingredients, query, types, cuisines, intolerances, maxTime, glutenFree, vegetarian, vegan, limit ->
                            val action = SearchFormFragmentDirections.actionSearchFormFragmentToDisplayResultsFragment(
                                ingredients,
                                query,
                                types,
                                cuisines,
                                intolerances,
                                maxTime,
                                glutenFree,
                                vegetarian,
                                vegan,
                                limit
                            )
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val useFoodSpecificModel = sharedPreferences.getBoolean(resources.getString(R.string.food_specific_ml_model_key), false)
        photoClassifier = PhotoClassifier(requireContext(), useFoodSpecificModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        photoClassifier.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFormScreen(
    detectedIngredientFromCamera: String?,
    showNoResultDialog: Boolean,
    onConfirmDetectedIngredient: () -> Unit,
    onDismissDetectedIngredient: () -> Unit,
    onDismissNoResultDialog: () -> Unit,
    onCameraClick: () -> Unit,
    onSubmit: (String, String, String, String, String, String, Boolean, Boolean, Boolean, String) -> Unit
) {
    var ingredients by remember { mutableStateOf(emptyList<String>()) }
    var query by remember { mutableStateOf("") }
    var selectedTypes by remember { mutableStateOf(emptySet<String>()) }
    var selectedCuisines by remember { mutableStateOf(emptySet<String>()) }
    var selectedIntolerances by remember { mutableStateOf(emptySet<String>()) }
    var maxReadyTime by remember { mutableIntStateOf(60) }
    var resultsLimit by remember { mutableIntStateOf(20) }
    var isGlutenFree by remember { mutableStateOf(false) }
    var isVegetarian by remember { mutableStateOf(false) }
    var isVegan by remember { mutableStateOf(false) }

    val resetFields = {
        query = ""
        ingredients = emptyList()
        selectedTypes = emptySet()
        selectedCuisines = emptySet()
        selectedIntolerances = emptySet()
        maxReadyTime = 60
        resultsLimit = 20
        isGlutenFree = false
        isVegetarian = false
        isVegan = false
    }

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
                                ingredients.joinToString(","),
                                query,
                                selectedTypes.joinToString(","),
                                selectedCuisines.joinToString(","),
                                selectedIntolerances.joinToString(","),
                                maxReadyTime.toString(),
                                isGlutenFree,
                                isVegetarian,
                                isVegan,
                                resultsLimit.toString()
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
                    Spacer(modifier = Modifier.height(0.dp)) // Top padding adjustment

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

                    Spacer(modifier = Modifier.height(16.dp)) // Bottom padding adjustment
                }
            }
        }
    }
}
