package com.apricot.app.ui.components

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.apricot.app.R
import com.apricot.app.ui.icons.book_2
import com.apricot.app.ui.theme.AppTheme

/**
 * An Extended FAB to open the link to the recipe instructions webpage
 */

@Composable
fun RecipeInstructionsExtendedFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        text = {
            Text(
                text = stringResource(id = R.string.show_recipe_instructions),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        icon = {
            Icon(
                imageVector = book_2,
                contentDescription = stringResource(R.string.content_description_recipe_instructions_fab),
            )
        },
        modifier = modifier,
        onClick = onClick,
        expanded = true,
    )
}

@Preview(showBackground = true)
@Composable
fun RecipeInstructionsExtendedFABPreview() {
    AppTheme {
        RecipeInstructionsExtendedFAB(
            onClick = {}
        )
    }
}