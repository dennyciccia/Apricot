package com.apricot.app.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.apricot.app.data.model.Recipe
import com.apricot.app.data.model.SearchParams
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
object HomeRoute

@Serializable
object SearchFormRoute

@Serializable
data class SearchResultsRoute(
    val searchParams: SearchParams
)

@Serializable
data class RecipeDetailsRoute(
    val recipe: Recipe
)

@Serializable
object FavouritesRoute

@Serializable
object SettingsRoute

// NavTypes are needed since SearchParams and Recipe are not primitive types
val SearchParamsNavType = object : NavType<SearchParams>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): SearchParams? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): SearchParams {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: SearchParams): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: SearchParams) {
        bundle.putString(key, Json.encodeToString(value))
    }
}

val RecipeNavType = object : NavType<Recipe>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Recipe? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Recipe {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: Recipe): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: Recipe) {
        bundle.putString(key, Json.encodeToString(value))
    }
}