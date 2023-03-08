package com.igrocery.overpriced.presentation.productlist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId

private const val ProductList = "productList"
private const val ProductList_Arg_CategoryId = "categoryId"
private const val ProductList_With_Args =
    "$ProductList?$ProductList_Arg_CategoryId={$ProductList_Arg_CategoryId}"

fun NavController.navigateToProductListScreen(
    categoryId: CategoryId? = null,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    var navString = ProductList
    if (categoryId != null) {
        require(categoryId.value > 0)
        navString += "?$ProductList_Arg_CategoryId=${categoryId.value}"
    }
    navigate(navString, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.productListScreen(
    navigateUp: () -> Unit,
    navigateToSearchProduct: () -> Unit,
    navigateToEditCategory: (CategoryId) -> Unit,
    navigateToProductDetail: (ProductId) -> Unit,
) {
    composable(
        ProductList_With_Args,
        listOf(navArgument(ProductList_Arg_CategoryId) {
            type = NavType.LongType
            defaultValue = 0L
        })
    ) { backStackEntry ->
        val productListScreenViewModel = hiltViewModel<ProductListScreenViewModel>()

        val args = ProductListScreenArgs(backStackEntry)

        ProductListScreen(
            viewModel = productListScreenViewModel,
            navigateUp = navigateUp,
            navigateToSearchProduct = navigateToSearchProduct,
            navigateToEditCategory = {
                requireNotNull(args.categoryId)
                navigateToEditCategory(args.categoryId)
             },
            navigateToProductDetail = navigateToProductDetail
        )
    }
}

internal data class ProductListScreenArgs(
    val categoryId: CategoryId?
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                categoryId = backStackEntry.arguments?.getLong(ProductList_Arg_CategoryId)
                    .takeIf { it != 0L }
                    ?.let { CategoryId(it) },
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                categoryId = savedStateHandle.get<Long>(ProductList_Arg_CategoryId)
                    .takeIf { it != 0L }
                    ?.let { CategoryId(it) },
            )
}
