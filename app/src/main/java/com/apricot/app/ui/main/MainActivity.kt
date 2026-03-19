package com.apricot.app.ui.main

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.apricot.app.R
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar management
        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)

        // Get the navController in order to move between fragments
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // This is for not showing the back arrow
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.searchFormFragment,
                R.id.displayResultsFragment,
                R.id.recipeDetailsFragment,
                R.id.favouritesFragment,
                R.id.settingsFragment
            )
        )

        // Set up the click listener in order to always return back to previous fragment
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favouritesFragment, R.id.settingsFragment -> {
                    if (navController.currentDestination?.id != menuItem.itemId)
                        navController.navigate(menuItem.itemId)
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                }
            }
        }

        // Return to home fragment by clicking on title, clean the backstack
        findViewById<TextView>(R.id.textViewTitle).setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
                navController.navigate(R.id.homeFragment, null, navOptions)
            }
        }
    }
}