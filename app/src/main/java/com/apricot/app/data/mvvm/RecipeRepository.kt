package com.apricot.app.data.mvvm

import com.apricot.app.data.database.FavouriteDao
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.model.SearchParams
import com.apricot.app.data.network.RecipeApiService
import com.apricot.app.ui.fragments.DisplayResultsFragmentArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository (
    private val api : RecipeApiService,
    private val dao : FavouriteDao
) {

    suspend fun findRecipes(searchArgs: SearchParams) : List<Recipe> {
        // Preparing request parameters
        val dietsSet: MutableSet<String> = mutableSetOf()
        if (searchArgs.glutenFree) dietsSet.add("Gluten Free")
        if (searchArgs.vegetarian) dietsSet.add("Vegetarian")
        if (searchArgs.vegan) dietsSet.add("Vegan")
        val diets = dietsSet.joinToString(",")

        // GET request with parameter parsing for Spoonacular API
        val response = api.findRecipes(
            includeIngredients = if (!searchArgs.ingredients.isNullOrEmpty()) searchArgs.ingredients.joinToString(",") else null,
            query = searchArgs.query,
            type = if (!searchArgs.recipeTypes.isNullOrEmpty()) searchArgs.recipeTypes.joinToString(",") else null,
            diet = if (!diets.isEmpty()) diets else null,
            intolerances = if (!searchArgs.intolerances.isNullOrEmpty()) searchArgs.intolerances.joinToString(",") else null,
            cuisine = if (!searchArgs.cuisines.isNullOrEmpty()) searchArgs.cuisines.joinToString(",") else null,
            maxReadyTime = searchArgs.maxPreparationTimeMinutes,
            number = searchArgs.resultsLimit,
            addRecipeInformation = true,
            fillIngredients = true
        )

        // Check if some recipes from the search results are already favorite
        val favouriteIds = dao.getAllFavouriteIds().toSet()
        val results = response.results.map {
            it.toRecipe().copy(isFavourite = favouriteIds.contains(it.id))
        }

        return results
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