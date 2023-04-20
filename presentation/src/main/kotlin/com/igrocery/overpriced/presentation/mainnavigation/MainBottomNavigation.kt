package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

const val MainBottomNavigation = "mainBottomNavigation"

fun NavController.navigateToMainBottomNavigationScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(MainBottomNavigation, builder)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainBottomNavigationScreen(
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
            mainBottomNavigationScreenViewModel = mainBottomNavigationScreenViewModel,
            navigateToSettings = navigateToSettings,
            navigateToEditGroceryList = navigateToEditGroceryList,
            navigateToSearchProduct = navigateToSearchProduct,
            navigateToProductList = navigateToProductList,
            navigateToNewPrice = navigateToNewPrice,
        )
    }
}
