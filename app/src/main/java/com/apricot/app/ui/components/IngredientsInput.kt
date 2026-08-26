package com.apricot.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apricot.app.R
import com.apricot.app.ui.theme.AppTheme

/**
 * A text input field that puts the user prompts into input chips in a flow row
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientsInput(
    onIngredientsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    ingredients: List<String> = emptyList(),
    label: String = stringResource(R.string.label_ingredients_input),
    placeholder: String = stringResource(R.string.placeholder_ingredients_input),
    onCameraClick: (() -> Unit)? = null
) {
    var text by remember { mutableStateOf("") }

    val addIngredient = {
        val trimmed = text.trim()
        if (trimmed.isNotEmpty() && !ingredients.contains(trimmed)) {
            onIngredientsChange(ingredients + trimmed)
            text = ""
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = onCameraClick?.let {
                {
                    IconButton(onClick = it) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = stringResource(R.string.content_description_scanner_fab)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { addIngredient() }
            )
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ingredients.forEach { ingredient ->
                InputChip(
                    selected = false,
                    onClick = {},
                    label = { Text(ingredient) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.ingredient_input_chip_content_description),
                            modifier = Modifier
                                .size(InputChipDefaults.IconSize)
                                .clickable {
                                    onIngredientsChange(ingredients - ingredient)
                                }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IngredientsInputPreview() {
    val ingredients = remember { mutableStateListOf("Tomato", "Mozzarella", "Basil") }
    AppTheme{
        IngredientsInput(
            onIngredientsChange = { newList ->
                ingredients.clear()
                ingredients.addAll(newList)
            },
            modifier = Modifier.padding(16.dp),
            ingredients = ingredients,
            onCameraClick = {}
        )
    }
}
