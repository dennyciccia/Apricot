package com.apricot.app.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.apricot.app.R
import com.apricot.app.ui.components.BottomNavigationBar
import com.apricot.app.ui.components.IngredientScannerFAB
import com.apricot.app.ui.theme.AppTheme
import com.apricot.app.ui.theme.Shapes

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreen() {
        var currentDestinationId by remember { mutableIntStateOf(R.id.homeFragment) }
        
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                // Return to home if clicking on title
                                navController?.let { controller ->
                                    if (controller.currentDestination?.id != R.id.homeFragment) {
                                        val navOptions = NavOptions.Builder()
                                            .setPopUpTo(R.id.homeFragment, false)
                                            .setLaunchSingleTop(true)
                                            .build()
                                        controller.navigate(R.id.homeFragment, null, navOptions)
                                    }
                                }
                            }
                        )
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    currentDestinationId = currentDestinationId,
                    onNavigate = { destinationId ->
                        navController?.let { controller ->
                            if (controller.currentDestination?.id != destinationId) {
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.homeFragment, false)
                                    .setLaunchSingleTop(true)
                                    .build()
                                controller.navigate(destinationId, null, navOptions)
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                val screensWithIngredientScannerFAB = listOf(R.id.homeFragment)
                if (currentDestinationId in screensWithIngredientScannerFAB) {
                    IngredientScannerFAB(modifier = Modifier) {
                        // TODO: chiama funzione per scannerizzare e manda richiesta http con quell'ingrediente e i filtri di default, invece se è nella schermata del form aggiunge l'ingrediente alla lista
                        Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                AndroidView(
                    factory = { context ->
                        LayoutInflater.from(context).inflate(R.layout.content_main, null).also {
                            val navHostFragment = supportFragmentManager
                                .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                            navController = navHostFragment.navController
                            
                            navController?.addOnDestinationChangedListener { _, destination, _ ->
                                currentDestinationId = destination.id
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
