package com.apricot.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.apricot.app.ui.theme.AppTheme

/**
 * A FAB for scanning an ingredient and call the classifier model.
 */
@Composable
fun IngredientScanner(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onScanClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Scansiona ingredienti"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IngredientScannerPreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            IngredientScanner(
                onScanClick = { /* Azione mock per ora */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}
