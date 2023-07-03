package com.igrocery.overpriced.presentation.mainnavigation.categorylist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.igrocery.overpriced.domain.CategoryId

const val CategoryList = "categoryList"

fun NavController.navigateToCategoryListScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(CategoryList, builder)
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.categoryListScreen(
    previousBackStackEntry: () -> NavBackStackEntry,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (CategoryId?) -> Unit,
) {
    composable(CategoryList) {
        val categoryListScreenViewModel =
            hiltViewModel<CategoryListScreenViewModel>(previousBackStackEntry())

        CategoryListScreen(
            topBarScrollBehavior = topBarScrollBehavior,
            categoryListScreenViewModel = categoryListScreenViewModel,
            navigateToSearchProduct = navigateToSearchProduct,
            navigateToProductList = navigateToProductList,
        )
    }
}
