package com.apricot.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apricot.app.data.model.FavouriteRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    // Insert a recipe into the database, if already present replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(recipe: FavouriteRecipeEntity)

    // Delete a recipe from the database
    @Delete
    suspend fun deleteFavourite(recipe: FavouriteRecipeEntity)

    // Get all recipes from the database, use flow so UI updates automatically
    @Query("SELECT * FROM favourite_recipes")
    fun getAllFavourites(): Flow<List<FavouriteRecipeEntity>>

    // Check if a recipe is already in the database (already favourite)
    @Query("SELECT EXISTS(SELECT * FROM favourite_recipes WHERE id = :recipeID)")
    suspend fun exists(recipeID: Int): Boolean

    // Get a single recipe by ID
    @Query("SELECT * FROM favourite_recipes WHERE id = :recipeID")
    suspend fun getRecipeByID(recipeID: Int): FavouriteRecipeEntity
}