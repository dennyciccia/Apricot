package com.apricot.app.data.mvvm

enum class AppThemeConfig {
    LIGHT, DARK, SYSTEM
}

data class UserPreferences(
    val useFoodSpecificMlModel: Boolean = false,
    val glutenFreeOnly: Boolean = false,
    val vegetarianOnly: Boolean = false,
    val veganOnly: Boolean = false,
    val cuisines: Set<String> = emptySet(),
    val intolerances: Set<String> = emptySet(),
    val maxReadyTime: Int? = null,
    val resultsLimit: Int? = null,
    val appColorTheme: AppThemeConfig = AppThemeConfig.SYSTEM
)
