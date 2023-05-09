package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.ProductId

private const val SearchProduct = "searchProduct"
private const val SearchProduct_Arg_Query = "query"
private const val SearchProduct_With_Args = "$SearchProduct/{$SearchProduct_Arg_Query}"

fun NavController.navigateToSearchProductScreen(
    query: String = "",
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate("$SearchProduct/${query}", builder)
}

@OptIn(ExperimentalAnimationApi::class)
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
