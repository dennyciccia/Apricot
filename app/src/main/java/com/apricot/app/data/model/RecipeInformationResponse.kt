package com.apricot.app.data.model

import com.google.gson.annotations.SerializedName

data class RecipeInformationResponse(
    val id: Int,
    val title: String,
    @SerializedName("image") val imageUrl: String,
    val readyInMinutes: Int,
    val sourceUrl: String,
    val dishTypes: List<String>,
) {
    fun toRecipe() : Recipe {
        return Recipe(
            id = this.id,
            title = this.title,
            imageUrl = this.imageUrl,
            readyInMinutes = this.readyInMinutes,
            sourceUrl = this.sourceUrl,
            dishTypes = this.dishTypes
        )
    }
}