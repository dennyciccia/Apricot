package com.apricot.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.apricot.app.R
import com.apricot.app.data.model.SearchParams
import com.apricot.app.data.mvvm.ScannerUiState
import com.apricot.app.ui.theme.AppTheme

@Composable
fun ScannerResultDialog(
    state: ScannerUiState,
    onConfirmSearch: (SearchParams) -> Unit,
    onRetryScan: () -> Unit,
    onDismiss: () -> Unit
) {
    when (state) {
        is ScannerUiState.ConfirmIngredient -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(text = stringResource(R.string.detected_ingredient_dialog_title))
                },
                text = {
                    Text(text = stringResource(R.string.found_ingredient_message, state.detectedIngredient))
                },
                confirmButton = {
                    TextButton(
                        onClick = { onConfirmSearch(state.searchParams) }
                    ) {
                        Text(text = stringResource(R.string.detected_ingredient_search_dialog))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onRetryScan
                    ) {
                        Text(text = stringResource(R.string.detected_ingredient_retry_dialog))
                    }
                }
            )
        }
        is ScannerUiState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(text = stringResource(R.string.detected_ingredient_dialog_title))
                },
                text = {
                    Text(text = stringResource(state.messageRes))
                },
                confirmButton = {
                    TextButton(
                        onClick = onRetryScan
                    ) {
                        Text(text = stringResource(R.string.no_ingredient_detected_dialog_confirm_label))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(text = stringResource(R.string.add_detected_ingredient_dialog_deny_label))
                    }
                }
            )
        }
        else -> {}
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerResultDialogConfirmPreview() {
    AppTheme {
        ScannerResultDialog(
            state = ScannerUiState.ConfirmIngredient(
                detectedIngredient = "Tomato",
                searchParams = SearchParams(ingredients = listOf("Tomato"))
            ),
            onConfirmSearch = {},
            onRetryScan = {},
            onDismiss = {}
        )

        ScannerResultDialog(
            state = ScannerUiState.Error(R.string.no_ingredient_detected_dialog_message),
            onConfirmSearch = {},
            onRetryScan = {},
            onDismiss = {}
        )
    }
}
