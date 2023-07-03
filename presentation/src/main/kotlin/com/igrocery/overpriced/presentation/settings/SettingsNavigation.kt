package com.igrocery.overpriced.presentation.settings

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable

const val Settings = "settings"

fun NavController.navigateToSettingsScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(Settings, builder)
}

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