package com.apricot.app.data.network

import com.apricot.app.data.model.ComplexSearchResponse
import com.apricot.app.data.model.RecipeInformationResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/complexSearch")
    suspend fun findRecipes(
        @Query("includeIngredients") includeIngredients: String?,
        @Query("query") query: String?,
        @Query("type") type: String?,
        @Query("diet") diet: String?,
        @Query("cuisine") cuisine: String?,
        @Query("intolerances") intolerances: String?,
        @Query("maxReadyTime") maxReadyTime: String?,
        @Query("number") number: String?
    ): ComplexSearchResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") id: Int
    ): RecipeInformationResponse
}