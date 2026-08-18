package com.apricot.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apricot.app.R
import com.apricot.app.ui.theme.AppTheme

/**
 * A compact card to show the recipes with the image on the left side
 * and the details on the right side.
 */
@Composable
fun CompactRecipeCard(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String? = null,
    availableIngredients: Int? = null,
    totalIngredients: Int? = null,
    prepTime: String? = null,
    isFavorite: Boolean = false,
    onCardClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
) {
    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recipe image on the left side
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(
                        R.string.recipe_card_image_content_description,
                        title
                    ),
                    modifier = Modifier
                        .width(110.dp)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )
            }

            // Recipe details on the right side
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Recipe title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Footer row: Available ingredents, time and favourite icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Ingredients availability
                    if (availableIngredients != null && totalIngredients != null) {
                        var textColor = MaterialTheme.colorScheme.onSurfaceVariant
                        var textWeight = FontWeight.Normal
                        if (availableIngredients == totalIngredients) {
                            textColor = MaterialTheme.colorScheme.primary
                            textWeight = FontWeight.Bold
                        }

                        Text(
                            text = stringResource(
                                R.string.X_frac_Y_ingredients,
                                availableIngredients, totalIngredients
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor,
                            fontWeight = textWeight,
                        )
                    }

                    // Preparation time
                    if (prepTime != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = prepTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Spacer per mantenere l'icona dei preferiti a destra se il tempo manca
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Favorite button
                    if (onFavoriteClick != null) {
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Rimuovi dai preferiti" else "Aggiungi ai preferiti",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompactRecipeCardPreview() {
    AppTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Caso completo
            CompactRecipeCard(
                title = "Pasta alla Carbonara",
                imageUrl = "https://example.com/pasta.jpg",
                availableIngredients = 5,
                totalIngredients = 5,
                prepTime = "20 min",
                isFavorite = true,
                onCardClick = {},
                onFavoriteClick = {}
            )

            CompactRecipeCard(
                title = "Pasta alla Carbonara senza panna mannaggia a voi",
                imageUrl = "https://example.com/pasta.jpg",
                availableIngredients = 3,
                totalIngredients = 5,
                isFavorite = true,
                onCardClick = {},
                onFavoriteClick = {}
            )

            CompactRecipeCard(
                title = "Pasta alla Carbonara senza panna mannaggia a voi su tre righe",
                imageUrl = "https://example.com/pasta.jpg",
                prepTime = "20 min",
                isFavorite = true,
                onCardClick = {},
                onFavoriteClick = {}
            )

            CompactRecipeCard(
                title = "Pasta alla Carbonara senza panna",
                imageUrl = "https://example.com/pasta.jpg",
                isFavorite = true,
                onCardClick = {},
                onFavoriteClick = {}
            )

            CompactRecipeCard(
                title = "Pasta alla Carbonara senza panna",
                imageUrl = "https://example.com/pasta.jpg",
                onCardClick = {},
            )

            // Caso con dati minimi
            CompactRecipeCard(
                title = "Ricetta Semplice",
                onCardClick = {}
            )
        }
    }
}
