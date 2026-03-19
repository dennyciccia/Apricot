package com.apricot.app.data.model

data class ComplexSearchResponse(
    val results: List<RecipeInComplexSearch>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)