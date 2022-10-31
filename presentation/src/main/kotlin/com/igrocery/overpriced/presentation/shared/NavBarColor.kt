package com.igrocery.overpriced.presentation.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

/**
 * Helpers for setting the system navigation bar color. Not to be confused with bottom navigation
 * bar.
 */

@Composable
fun DefaultSystemNavBarColor() {
    val systemUiController = rememberSystemUiController()
    val navBarColor = MaterialTheme.colorScheme.surface
    SideEffect {
        systemUiController.setNavigationBarColor(
            navBarColor,
            navigationBarContrastEnforced = false,
            transformColorForLightContent = { color -> color })
    }
}

@Composable
fun UseDefaultBottomNavBarColourForSystemNavBarColor() {
    // this is the same color used for the bottom nav bar (default)
    val systemUiController = rememberSystemUiController()
    val navBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp)
    SideEffect {
        systemUiController.setNavigationBarColor(
            navBarColor,
            navigationBarContrastEnforced = false,
            transformColorForLightContent = { color -> color })
    }
}