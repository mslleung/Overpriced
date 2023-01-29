package com.igrocery.overpriced.presentation.storepricedetail

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId

private const val StorePriceDetail = "StorePriceDetail"
private const val StorePriceDetail_Arg_ProductId = "productId"
private const val StorePriceDetail_Arg_StoreId = "storeId"
private const val StorePriceDetail_With_Args =
    "$StorePriceDetail/{$StorePriceDetail_Arg_ProductId}/{$StorePriceDetail_Arg_StoreId}"

fun NavController.navigateToStorePriceDetailScreen(
    productId: ProductId,
    storeId: StoreId,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    require(productId.value > 0)
    require(storeId.value > 0)
    navigate("$StorePriceDetail/$productId/$storeId", builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.storePriceDetailScreen(
    navigateUp: () -> Unit,
) {
    composable(
        StorePriceDetail_With_Args,
        arguments = listOf(
            navArgument(StorePriceDetail_Arg_ProductId) {
                type = NavType.LongType
            },
            navArgument(StorePriceDetail_Arg_StoreId) {
                type = NavType.LongType
            }
        )
    ) {
        val storePriceDetailScreenViewModel = hiltViewModel<StorePriceDetailScreenViewModel>()

        StorePriceDetailScreen(
            viewModel = storePriceDetailScreenViewModel,
            navigateUp = navigateUp,
        )
    }
}

internal class StorePriceDetailScreenArgs(
    val productId: ProductId,
    val storeId: StoreId
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                productId = ProductId(
                    backStackEntry.arguments?.getLong(StorePriceDetail_Arg_ProductId)
                        ?: throw IllegalArgumentException("productId should not be null")
                ),
                storeId = StoreId(
                    backStackEntry.arguments?.getLong(StorePriceDetail_Arg_StoreId)
                        ?: throw IllegalArgumentException("storeId should not be null")
                )
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                productId = savedStateHandle.get<ProductId>(StorePriceDetail_Arg_ProductId)
                    .takeIf { it?.value != 0L }
                    ?: throw IllegalArgumentException("productId should not be null"),
                storeId = savedStateHandle.get<StoreId>(StorePriceDetail_Arg_StoreId)
                    .takeIf { it?.value != 0L }
                    ?: throw IllegalArgumentException("storeId should not be null")
            )
}
