package com.igrocery.overpriced.presentation.mainnavigation.categorylist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.igrocery.overpriced.domain.productpricehistory.models.Category

private const val CategoryList = "categoryList"

fun NavController.navigateToCategoryListScreen(navOptions: NavOptions? = null) {
    navigate(CategoryList, navOptions)
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.categoryListScreen(
    rootBackStackEntry: NavBackStackEntry,
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (Category?) -> Unit,
) {
    composable(CategoryList) {
        val categoryListScreenViewModel =
            hiltViewModel<CategoryListScreenViewModel>(rootBackStackEntry)

        CategoryListScreen(
            topBarScrollBehavior = topBarScrollBehavior,
            categoryListScreenViewModel = categoryListScreenViewModel,
            navigateToSearchProduct = navigateToSearchProduct,
            navigateToProductList = navigateToProductList,
        )
    }
}
