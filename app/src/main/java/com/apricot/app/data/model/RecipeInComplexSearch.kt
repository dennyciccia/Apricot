package com.apricot.app.data.model

import com.google.gson.annotations.SerializedName

data class RecipeInComplexSearch(
    val id: Int,
    val title: String,
    @SerializedName("image") val imageUrl: String,
) {
    fun toRecipe() : Recipe {
        return Recipe(
            id = this.id,
            title = this.title,
            imageUrl = this.imageUrl
        )
    }
}