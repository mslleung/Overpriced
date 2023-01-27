package com.igrocery.overpriced.presentation.mainnavigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.igrocery.overpriced.domain.productpricehistory.models.Category

const val MainBottomNavigation = "mainBottomNavigation"

fun NavController.navigateToMainBottomNavigationScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(MainBottomNavigation, builder)
}

fun NavGraphBuilder.mainBottomNavigationScreen(
    bottomNavController: NavHostController,
    navigateToSettings: () -> Unit,

    // forwarded navigation from ShoppingList
    navigateToEditGroceryList: (groceryListId: Long) -> Unit,

    // forwarded navigation from CategoryList
    navigateToSearchProduct: () -> Unit,
    navigateToProductList: (Category?) -> Unit,
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
