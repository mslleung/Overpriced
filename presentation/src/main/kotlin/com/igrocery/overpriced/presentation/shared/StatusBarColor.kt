package com.igrocery.overpriced.presentation.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun UseDefaultStatusBarColor() {
    val statusBarColor = MaterialTheme.colorScheme.surface

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            statusBarColor,
            transformColorForLightContent = { color -> color })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseScrollFadeSurfaceStatusBarColor(topAppBarState: TopAppBarState) {
    val fraction by remember {
        derivedStateOf {
            log.error("collapsedFraction " + topAppBarState.collapsedFraction)
            if (topAppBarState.collapsedFraction > 0.01f) 1f else 0f
        }
    }
    log.error("fraction $topAppBarState.overlappedFraction")

    val containerColor = MaterialTheme.colorScheme.surface
    val scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp)
    val targetColor = lerp(
        containerColor,
        scrolledContainerColor,
        FastOutLinearInEasing.transform(fraction)
    )

    val currentColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(currentColor) {
        systemUiController.setStatusBarColor(
            currentColor,
            transformColorForLightContent = { color -> color })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseScrollLerpSurfaceStatusBarColor(topAppBarState: TopAppBarState) {
    val containerColor = MaterialTheme.colorScheme.surface
    val scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp)
    val targetColor = lerp(
        containerColor,
        scrolledContainerColor,
        FastOutLinearInEasing.transform(topAppBarState.collapsedFraction)
    )

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            targetColor,
            transformColorForLightContent = { color -> color })
    }
}
