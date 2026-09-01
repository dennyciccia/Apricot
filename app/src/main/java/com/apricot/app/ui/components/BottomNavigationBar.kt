package com.apricot.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.apricot.app.R
import com.apricot.app.data.mvvm.AppThemeConfig
import com.apricot.app.ui.navigation.FavouritesRoute
import com.apricot.app.ui.navigation.HomeRoute
import com.apricot.app.ui.navigation.SearchFormRoute
import com.apricot.app.ui.navigation.SettingsRoute
import com.apricot.app.ui.theme.AppTheme
import kotlin.reflect.KClass

private data class NavigationItem<T : Any>(
    val route: T,
    val routeClass: KClass<T>,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    onNavigate: (Any) -> Unit
) {
    val items = listOf(
        NavigationItem(HomeRoute, HomeRoute::class, stringResource(R.string.home), Icons.Default.Home),
        NavigationItem(SearchFormRoute, SearchFormRoute::class, stringResource(R.string.search_label), Icons.Default.Search),
        NavigationItem(FavouritesRoute, FavouritesRoute::class, stringResource(R.string.favourites), Icons.Default.Favorite),
        NavigationItem(SettingsRoute, SettingsRoute::class, stringResource(R.string.settings), Icons.Default.Settings)
    )

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination?.hasRoute(item.routeClass) == true
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    AppTheme(themeConfig = AppThemeConfig.LIGHT) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BottomNavigationBar(
                currentDestination = null,
                onNavigate = {}
            )
        }
    }
}