package com.igrocery.overpriced.presentation.selectcurrency

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable

private const val SelectCurrency = "selectCurrency"

fun NavController.navigateToSelectCurrencyScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(SelectCurrency, builder)
}

fun NavGraphBuilder.selectCurrencyScreen(
    navigateUp: () -> Unit,
) {
    composable(SelectCurrency) {
        val selectCurrencyViewModel = hiltViewModel<SelectCurrencyScreenViewModel>()

        SelectCurrencyScreen(
            viewModel = selectCurrencyViewModel,
            navigateUp = navigateUp,
        )
    }
}
