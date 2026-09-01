package com.apricot.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchParams(
    val ingredients: List<String>? = null,
    val query: String? = null,
    val recipeTypes: Set<String>? = null,
    val glutenFree: Boolean = false,
    val vegan: Boolean = false,
    val vegetarian: Boolean = false,
    val intolerances: Set<String>? = null,
    val cuisines: Set<String>? = null,
    val maxPreparationTimeMinutes: Int? = null,
    val resultsLimit: Int? = null,
)
