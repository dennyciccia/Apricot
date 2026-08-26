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
import com.apricot.app.R
import com.apricot.app.ui.theme.AppTheme

@Composable
fun BottomNavigationBar(
    currentDestinationId: Int,
    onNavigate: (Int) -> Unit
) {
    val items = listOf(
        NavigationItem(R.id.homeFragment, stringResource(R.string.home), Icons.Default.Home),
        NavigationItem(R.id.searchFormFragment, stringResource(R.string.search_label), Icons.Default.Search),
        NavigationItem(R.id.favouritesFragment, stringResource(R.string.favourites), Icons.Default.Favorite),
        NavigationItem(R.id.settingsFragment, stringResource(R.string.settings), Icons.Default.Settings)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestinationId == item.destinationId,
                onClick = { onNavigate(item.destinationId) }
            )
        }
    }
}

private data class NavigationItem(
    val destinationId: Int,
    val label: String,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    AppTheme(darkTheme = false) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BottomNavigationBar(
                currentDestinationId = R.id.homeFragment,
                onNavigate = {}
            )

            BottomNavigationBar(
                currentDestinationId = R.id.searchFormFragment,
                onNavigate = {}
            )

            BottomNavigationBar(
                currentDestinationId = R.id.favouritesFragment,
                onNavigate = {}
            )

            BottomNavigationBar(
                currentDestinationId = R.id.settingsFragment,
                onNavigate = {}
            )
        }
    }
}