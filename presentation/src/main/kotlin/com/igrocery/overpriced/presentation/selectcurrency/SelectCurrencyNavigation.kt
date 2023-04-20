package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable

private const val SelectCurrency = "selectCurrency"

fun NavController.navigateToSelectCurrencyScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(SelectCurrency, builder)
}

@OptIn(ExperimentalAnimationApi::class)
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
