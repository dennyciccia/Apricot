package com.apricot.app.data.mvvm

import com.apricot.app.data.database.FavouriteDao
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.model.RecipeInComplexSearch
import com.apricot.app.data.network.RecipeApiService
import com.apricot.app.ui.fragments.DisplayResultsFragmentArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository (
    private val api : RecipeApiService,
    private val dao : FavouriteDao
) {

    suspend fun findRecipes(searchArgs: DisplayResultsFragmentArgs) : List<RecipeInComplexSearch> {
        // Preparing request parameters
        var diets = ""
        if (searchArgs.glutenFree) diets += "Gluten Free,"
        if (searchArgs.vegetarian) diets += "Vegetarian"
        if (searchArgs.vegan) diets += "Vegan"

        // GET request
        val response = api.findRecipes(
            if (!searchArgs.ingredients.isEmpty()) searchArgs.ingredients else null,
            if (!searchArgs.query.isEmpty()) searchArgs.query else null,
            if (!searchArgs.types.isEmpty()) searchArgs.types else null,
            if (!diets.isEmpty()) diets else null,
            if (!searchArgs.cuisines.isEmpty()) searchArgs.cuisines else null,
            if (!searchArgs.intolerances.isEmpty()) searchArgs.intolerances else null,
            if (!searchArgs.maxReadyTime.isEmpty()) searchArgs.maxReadyTime else null,
            if (!searchArgs.resultsLimit.isEmpty()) searchArgs.resultsLimit else "100"
        )
        return response.results
    }

    suspend fun getRecipeDetails(recipeID: Int, fromNetwork: Boolean): Recipe {
        return if (fromNetwork) {
            val isAlreadySaved = dao.exists(recipeID)
            api.getRecipeDetails(recipeID).toRecipe().copy(isFavourite = isAlreadySaved)
        } else {
            dao.getRecipeByID(recipeID).toRecipe().copy(isFavourite = true)
        }
    }

    suspend fun saveAsFavorite(recipe: Recipe) {
        dao.insertFavourite(recipe.toFavouriteRecipeEntity())
    }

    suspend fun removeFromFavorites(recipe: Recipe) {
        dao.deleteFavourite(recipe.toFavouriteRecipeEntity())
    }

    fun getFavouriteRecipes(): Flow<List<Recipe>> {
        return dao.getAllFavourites().map { entityList ->
            entityList.map { entity ->
                entity.toRecipe() }
        }
    }
}