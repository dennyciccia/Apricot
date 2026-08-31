package com.apricot.app.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object SearchFormRoute

@Serializable
data class SearchResultsRoute(
    val ingredients: List<String>? = null,
    val query: String? = null,
    val recipeTypes: List<String>? = null,
    val glutenFree: Boolean = false,
    val vegan: Boolean = false,
    val vegetarian: Boolean = false,
    val intolerances: List<String>? = null,
    val cuisines: List<String>? = null,
    val maxPreparationTimeMinutes: Int? = null,
    val resultsLimit: Int? = null,
)

@Serializable
data class RecipeDetailsRoute(
    val recipeID: Int,
    val dataFromNetwork: Boolean,
)

@Serializable
object FavouritesRoute

@Serializable
object SettingsRoute