package com.apricot.app.data.model

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
    val dishTypes: List<String>? = null,
    val isFavourite: Boolean = false
) {
    fun toFavouriteRecipeEntity() : FavouriteRecipeEntity {
        return FavouriteRecipeEntity(
            id = this.id,
            title = this.title,
            imageUrl = this.imageUrl,
            readyInMinutes = this.readyInMinutes!!,
            sourceUrl = this.sourceUrl!!,
            cuisines = this.cuisines!!.joinToString(","),
            glutenFree = this.glutenFree!!,
            sustainable = this.sustainable!!,
            vegan = this.vegan!!,
            vegetarian = this.vegetarian!!,
            dishTypes = this.dishTypes!!.joinToString(",")
        )
    }
}
