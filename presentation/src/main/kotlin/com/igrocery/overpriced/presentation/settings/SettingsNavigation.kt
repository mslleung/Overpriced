package com.igrocery.overpriced.presentation.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable

const val Settings = "settings"

fun NavController.navigateToSettingsScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(Settings, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsScreen(
    navigateUp: () -> Unit,
    navigateToSelectCurrencyScreen: () -> Unit,
) {
    composable(Settings) {
        val settingsViewModel = hiltViewModel<SettingsScreenViewModel>()

        SettingsScreen(
            viewModel = settingsViewModel,
            navigateUp = navigateUp,
            navigateToSelectCurrencyScreen = navigateToSelectCurrencyScreen
        )
    }
}