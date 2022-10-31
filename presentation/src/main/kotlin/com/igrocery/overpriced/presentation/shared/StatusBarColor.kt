package com.igrocery.overpriced.presentation.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DefaultStatusBarColor() {
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
fun FadeSurfaceStatusBarColor(topAppBarState: TopAppBarState) {
    val fraction = if (topAppBarState.collapsedFraction > 0.01f) 1f else 0f

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
    SideEffect {
        systemUiController.setStatusBarColor(
            currentColor,
            transformColorForLightContent = { color -> color })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LerpSurfaceStatusBarColor(topAppBarState: TopAppBarState) {
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

//@Composable
//fun PinnedScrollStatusBarColor(is)