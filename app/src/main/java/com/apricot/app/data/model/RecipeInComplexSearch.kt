package com.apricot.app.data.model

import com.google.gson.annotations.SerializedName

data class RecipeInComplexSearch(
    val id: Int,
    val title: String,
    @SerializedName("image") val imageUrl: String,
    val readyInMinutes: Int? = null,
    val sourceUrl: String? = null,
    val cuisines: List<String>? = null,
    val glutenFree: Boolean? = null,
    val sustainable: Boolean? = null,
    val vegan: Boolean? = null,
    val vegetarian: Boolean? = null,
    @SerializedName("dishTypes") val recipeTypes: List<String>? = null,
    val usedIngredientCount: Int? = null,
    val missedIngredientCount: Int? = null
) {
    fun toRecipe() : Recipe {
        return Recipe(
            id = this.id,
            title = this.title,
            imageUrl = this.imageUrl,
            readyInMinutes = this.readyInMinutes,
            sourceUrl = this.sourceUrl,
            cuisines = this.cuisines,
            glutenFree = this.glutenFree,
            sustainable = this.sustainable,
            vegan = this.vegan,
            vegetarian = this.vegetarian,
            recipeTypes = this.recipeTypes,
            usedIngredientCount = this.usedIngredientCount,
            missedIngredientCount = this.missedIngredientCount
        )
    }
}