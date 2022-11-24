package com.igrocery.overpriced.presentation.pricelist

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun PriceListScreen(
    viewModel: PriceListScreenViewModel,
    navigateUp: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing PriceListScreen")

    val state by rememberPriceListScreenState()
    MainContent(
        viewModelState = viewModel,
        state = state,
        onBackButtonClick = navigateUp,
        onSearchButtonClick = navigateToSearchProduct,
        onEditButtonClick = navigateToEditCategory,
        onProductClick = {},
        modifier = modifier
    )

    BackHandler {
        log.debug("Composing PriceListScreen: BackHandler")
        navigateUp()
    }
}

@Composable
private fun MainContent(

) {

}
