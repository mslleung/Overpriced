package com.igrocery.overpriced.presentation.newprice

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Store

private const val NewPrice = "newPrice"
const val NewPrice_Arg_ProductId = "productId"
const val NewPrice_Arg_CategoryId = "categoryId"
private const val NewPrice_With_Args =
    "$NewPrice?$NewPrice_Arg_ProductId={$NewPrice_Arg_ProductId}?$NewPrice_Arg_CategoryId={$NewPrice_Arg_CategoryId}"

fun NavController.navigateToNewPriceScreen(
    productId: ProductId? = null,
    categoryId: CategoryId? = null,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    var navString = NewPrice
    if (productId != null) {
        require(productId.value > 0)
        navString += "?$NewPrice_Arg_ProductId={$productId}"
    }
    if (categoryId != null) {
        require(categoryId.value > 0)
        navString += "?$NewPrice_Arg_CategoryId={$categoryId}"
    }
    navigate(navString, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.newPriceScreen(
    navigateUp: () -> Unit,
    navigateToNewCategory: () -> Unit,
    navigateToEditCategory: (Category) -> Unit,
    navigateToNewStore: () -> Unit,
    navigateToEditStore: (StoreId) -> Unit,
) {
    composable(
        NewPrice_With_Args,
        arguments = listOf(
            navArgument(NewPrice_Arg_ProductId) {
                type = NavType.LongType
                defaultValue = 0L
            },
            navArgument(NewPrice_Arg_CategoryId) {
                type = NavType.LongType
                defaultValue = 0L
            }
        )
    ) { backStackEntry ->
        val newPriceViewModel = hiltViewModel<NewPriceScreenViewModel>()

        NewPriceScreen(
            savedStateHandle = backStackEntry.savedStateHandle,
            newPriceScreenViewModel = newPriceViewModel,
            navigateUp = navigateUp,
            navigateToNewCategory = navigateToNewCategory,
            navigateToEditCategory = navigateToEditCategory,
            navigateToNewStore = navigateToNewStore,
            navigateToEditStore = navigateToEditStore,
        )
    }
}

internal class NewPriceScreenArgs(
    val productId: ProductId?,
    val categoryId: CategoryId?
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                productId = backStackEntry.arguments?.getLong(NewPrice_Arg_ProductId)
                    .takeIf { it != 0L }
                    ?.let { ProductId(it) },
                categoryId = backStackEntry.arguments?.getLong(NewPrice_Arg_CategoryId)
                    .takeIf { it != 0L }
                    ?.let { CategoryId(it) }
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                productId = savedStateHandle.get<ProductId>(NewPrice_Arg_ProductId),
                categoryId = savedStateHandle.get<CategoryId>(NewPrice_Arg_CategoryId)
            )
}
