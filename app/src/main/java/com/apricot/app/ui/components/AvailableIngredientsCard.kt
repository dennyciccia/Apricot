package com.apricot.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apricot.app.data.model.Recipe

/**
 * A card to show ingredients availability
 */

@Composable
fun AvailableIngredientsCard(
    modifier: Modifier = Modifier,
    recipe: Recipe,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Kitchen,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.width(12.dp))
            Column {
                val availableIngredients = recipe.usedIngredientCount ?: 0
                val missedIngredients = recipe.missedIngredientCount ?: 0
                val totalIngredients = availableIngredients + missedIngredients

                Text(
                    text = "$availableIngredients/$totalIngredients available ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { if (totalIngredients > 0) availableIngredients.toFloat() / totalIngredients else 0f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}