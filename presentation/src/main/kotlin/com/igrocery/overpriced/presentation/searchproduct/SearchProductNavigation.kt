package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.ProductId

private const val SearchProduct = "searchProduct"

fun NavController.navigateToSearchProductScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(SearchProduct, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.searchProductScreen(
    navigateUp: () -> Unit,
    navigateToProductDetails: (ProductId) -> Unit,
) {
    composable(SearchProduct) {
        val searchProductScreenViewModel = hiltViewModel<SearchProductScreenViewModel>()

        SearchProductScreen(
            viewModel = searchProductScreenViewModel,
            navigateUp = navigateUp,
            navigateToProductDetails = navigateToProductDetails
        )
    }
}
