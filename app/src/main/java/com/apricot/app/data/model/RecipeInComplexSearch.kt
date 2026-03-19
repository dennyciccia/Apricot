package com.apricot.app.data.model

import com.google.gson.annotations.SerializedName

data class RecipeInComplexSearch(
    override val id: Int,
    override val title: String,
    @SerializedName("image") override val imageUrl: String,
) : AbstractRecipe()