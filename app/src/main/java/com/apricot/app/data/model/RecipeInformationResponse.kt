package com.apricot.app.data.model

import com.google.gson.annotations.SerializedName

data class RecipeInformationResponse(
    val id: Int,
    val title: String,
    @SerializedName("image") val imageUrl: String,
    val readyInMinutes: Int,
    val sourceUrl: String,
    val pricePerServing: Double,
    val cuisines: List<String>,
    val glutenFree: Boolean,
    val sustainable: Boolean,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val dishTypes: List<String>
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
            dishTypes = this.dishTypes,
            isFavourite = false
        )
    }
}