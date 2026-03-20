package com.apricot.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_recipes")
data class FavouriteRecipeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val readyInMinutes: Int,
    val sourceUrl: String,
    val cuisines: String,
    val glutenFree: Boolean,
    val sustainable: Boolean,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val dishTypes: String
) {
    fun toRecipe() : Recipe {
        return Recipe(
            id = this.id,
            title = this.title,
            imageUrl = this.imageUrl,
            readyInMinutes = this.readyInMinutes,
            sourceUrl = this.sourceUrl,
            cuisines = this.cuisines.split(","),
            glutenFree = this.glutenFree,
            sustainable = this.sustainable,
            vegan = this.vegan,
            vegetarian = this.vegetarian,
            dishTypes = this.dishTypes.split(","),
            isFavourite = true
        )
    }
}