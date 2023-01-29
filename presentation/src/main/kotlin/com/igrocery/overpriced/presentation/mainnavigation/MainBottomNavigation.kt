package com.igrocery.overpriced.presentation.mainnavigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.GroceryListId

const val MainBottomNavigation = "mainBottomNavigation"

fun NavController.navigateToMainBottomNavigationScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(MainBottomNavigation, builder)
}

fun NavGraphBuilder.mainBottomNavigationScreen(
    bottomNavController: NavHostController,
    navigateToSettings: () -> Unit,

    // forwarded navigation from ShoppingList
    navigateToEditGroceryList: (GroceryListId) -> Unit,

    // forwarded navigation from CategoryList
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (CategoryId?) -> Unit,
    navigateToNewPrice: () -> Unit,
) {
    composable(MainBottomNavigation) {
        val mainBottomNavigationScreenViewModel =
            hiltViewModel<MainBottomNavigationScreenViewModel>()

        MainBottomNavigationScreen(
            bottomNavController = bottomNavController,
            mainBottomNavigationScreenViewModel = mainBottomNavigationScreenViewModel,
            navigateToSettings = navigateToSettings,
            navigateToEditGroceryList = navigateToEditGroceryList,
            navigateToSearchProduct = navigateToSearchProduct,
            navigateToProductList = navigateToProductList,
            navigateToNewPrice = navigateToNewPrice,
        )
    }
}
