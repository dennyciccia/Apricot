package com.apricot.app.data.model

data class Recipe(
    override val id: Int,
    override val title: String,
    override val imageUrl: String,
    val readyInMinutes: Int,
    val sourceUrl: String,
    val dishTypes: List<String>,
    val isFavourite: Boolean = false,
) : AbstractRecipe() {
    fun toFavouriteRecipeEntity() : FavouriteRecipeEntity {
        return FavouriteRecipeEntity(
            id = this.id,
            title = this.title,
            imageUrl = this.imageUrl,
            readyInMinutes = this.readyInMinutes,
            sourceUrl = this.sourceUrl,
            dishTypes = this.dishTypes.joinToString(", ")
        )
    }
}
