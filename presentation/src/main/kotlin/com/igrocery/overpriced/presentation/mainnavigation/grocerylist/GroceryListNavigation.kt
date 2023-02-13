package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.presentation.mainnavigation.MainBottomNavigationScreenViewModelState

const val GroceryList = "groceryList"

fun NavController.navigateToGroceryListScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(GroceryList, builder)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
fun NavGraphBuilder.groceryListScreen(
    previousBackStackEntry: () -> NavBackStackEntry,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    mainBottomNavigationViewModelState: MainBottomNavigationScreenViewModelState,
    navigateToEditGroceryList: (GroceryListId) -> Unit,
) {
    composable(GroceryList) {
        val groceryListScreenViewModel =
            hiltViewModel<GroceryListScreenViewModel>(previousBackStackEntry())

        GroceryListScreen(
            topBarScrollBehavior = topBarScrollBehavior,
            mainBottomNavigationViewModelState = mainBottomNavigationViewModelState,
            groceryListScreenViewModel = groceryListScreenViewModel,
            navigateToEditGroceryList = navigateToEditGroceryList
        )
    }
}
