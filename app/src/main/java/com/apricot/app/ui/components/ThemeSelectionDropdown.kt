package com.apricot.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.apricot.app.R
import com.apricot.app.data.mvvm.AppThemeConfig


/**
 * A dropdown to set app theme as either light, dark or system
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionDropdown(
    selectedTheme: AppThemeConfig,
    onThemeChange: (AppThemeConfig) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val themes = listOf(
        AppThemeConfig.LIGHT to stringResource(R.string.theme_light),
        AppThemeConfig.DARK to stringResource(R.string.theme_dark),
        AppThemeConfig.SYSTEM to stringResource(R.string.theme_system)
    )

    val selectedThemeLabel = themes.find { it.first == selectedTheme }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedThemeLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.theme_setting_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            themes.forEach { (theme, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onThemeChange(theme)
                        expanded = false
                    }
                )
            }
        }
    }
}
