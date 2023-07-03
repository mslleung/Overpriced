package com.igrocery.overpriced.presentation.searchproduct

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.igrocery.overpriced.domain.ProductId

private const val SearchProduct = "searchProduct"
private const val SearchProduct_Arg_Query = "query"
private const val SearchProduct_With_Args = "$SearchProduct?$SearchProduct_Arg_Query={$SearchProduct_Arg_Query}"

fun NavController.navigateToSearchProductScreen(
    query: String? = null,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    var navString = SearchProduct
    if (query != null) {
        navString += "?$SearchProduct_Arg_Query=${query}"
    }
    navigate(navString, builder)
}

fun NavGraphBuilder.searchProductScreen(
    navigateUp: () -> Unit,
    navigateToProductDetails: (ProductId) -> Unit,
) {
    composable(
        route = SearchProduct_With_Args,
        arguments = listOf(navArgument(SearchProduct_Arg_Query) {
            type = NavType.StringType
            defaultValue = ""
        })
    ) { backStackEntry ->
        val searchProductScreenViewModel = hiltViewModel<SearchProductScreenViewModel>()

        val args = SearchProductScreenArgs(backStackEntry)

        SearchProductScreen(
            viewModel = searchProductScreenViewModel,
            navigateUp = navigateUp,
            navigateToProductDetails = navigateToProductDetails
        )
    }
}

internal data class SearchProductScreenArgs(
    val query: String
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                query = backStackEntry.arguments?.getString(SearchProduct_Arg_Query)
                    ?: throw IllegalArgumentException("query should not be null")
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                query = savedStateHandle.get<String>(SearchProduct_Arg_Query)
                    ?: throw IllegalArgumentException("query should not be null")
            )
}
