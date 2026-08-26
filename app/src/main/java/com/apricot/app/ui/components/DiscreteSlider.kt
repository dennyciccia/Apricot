package com.apricot.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A slider with a text input on the right side used to input integer values.
 * This component follows the state hoisting pattern.
 */
@Composable
fun DiscreteSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int = 100,
    textFieldSuffix: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = min.toFloat()..max.toFloat(),
            steps = if (max - min > 1) max - min - 1 else 0,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }
                if (filtered.isNotEmpty()) {
                    val parsed = filtered.toIntOrNull() ?: value
                    onValueChange(parsed.coerceIn(min, max))
                }
            },
            modifier = Modifier.width(90.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            suffix = textFieldSuffix,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiscreteSliderPreview() {
    var value by remember { mutableIntStateOf(50) }
    Surface(modifier = Modifier.padding(16.dp)) {
        DiscreteSlider(
            value = value,
            onValueChange = { value = it },
            min = 10,
            max = 200,
            textFieldSuffix = { Text("units") }
        )
    }
}
