package com.apricot.app.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
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
    val recipeID: Int,
    val dataFromNetwork: Boolean,
)

@Serializable
object FavouritesRoute

@Serializable
object SettingsRoute

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
