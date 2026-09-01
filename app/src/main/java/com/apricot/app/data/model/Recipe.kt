package com.apricot.app.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Recipe(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val readyInMinutes: Int? = null,
    val sourceUrl: String? = null,
    val cuisines: List<String>? = null,
    val glutenFree: Boolean? = null,
    val sustainable: Boolean? = null,
    val vegan: Boolean? = null,
    val vegetarian: Boolean? = null,
    val recipeTypes: List<String>? = null,
    val isFavourite: Boolean = false,
    val usedIngredientCount: Int? = null,
    val missedIngredientCount: Int? = null
) {
    fun toFavouriteRecipeEntity() : FavouriteRecipeEntity {
        return FavouriteRecipeEntity(
            id = this.id,
            title = this.title,
            imageUrl = this.imageUrl,
            readyInMinutes = this.readyInMinutes ?: 0,
            sourceUrl = this.sourceUrl ?: "",
            cuisines = this.cuisines?.joinToString(",") ?: "",
            glutenFree = this.glutenFree ?: false,
            sustainable = this.sustainable ?: false,
            vegan = this.vegan ?: false,
            vegetarian = this.vegetarian ?: false,
            recipeTypes = this.recipeTypes?.joinToString(",") ?: ""
        )
    }
}
