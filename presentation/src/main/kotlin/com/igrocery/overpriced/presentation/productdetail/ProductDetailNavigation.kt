package com.igrocery.overpriced.presentation.productdetail

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId

private const val ProductDetail = "ProductDetail"
private const val ProductDetail_Arg_ProductId = "productId"
private const val ProductDetail_With_Args =
    "$ProductDetail/{$ProductDetail_Arg_ProductId}"


fun NavController.navigateToProductDetailScreen(
    productId: ProductId,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    require(productId.value > 0)
    navigate("$ProductDetail/$productId", builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.productDetailScreen(
    navigateUp: () -> Unit,
    navigateToStorePriceDetail: (ProductId, StoreId) -> Unit,
) {
    composable(
        ProductDetail_With_Args,
        arguments = listOf(navArgument(ProductDetail_Arg_ProductId) {
            type = NavType.LongType
        })
    ) {backStackEntry ->
        val productDetailViewModel = hiltViewModel<ProductDetailScreenViewModel>()

        val args = ProductDetailScreenArgs(backStackEntry)

        ProductDetailScreen(
            viewModel = productDetailViewModel,
            navigateUp = navigateUp,
            navigateToStorePriceDetail = { navigateToStorePriceDetail(args.productId, it) }
        )
    }
}

internal class ProductDetailScreenArgs(
    val productId: ProductId
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                productId = ProductId(
                    backStackEntry.arguments?.getLong(ProductDetail_Arg_ProductId)
                        ?: throw IllegalArgumentException("productId should not be null")
                )
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                productId = savedStateHandle.get<ProductId>(ProductDetail_Arg_ProductId)
                    .takeIf { it?.value != 0L }
                    ?: throw IllegalArgumentException("productId should not be null")
            )
}
