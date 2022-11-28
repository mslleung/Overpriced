package com.igrocery.overpriced.presentation.productdetail

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.igrocery.overpriced.presentation.productlist.ProductListScreenViewModel
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun ProductDetailScreen(
    viewModel: ProductListScreenViewModel,
    navigateUp: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    log.debug("Composing ProductDetailScreen")

    val state by rememberProductDetailScreenState()
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
        log.debug("ProductDetailScreen: BackHandler")
        navigateUp()
    }
}

@Composable
private fun MainContent(

) {

}
