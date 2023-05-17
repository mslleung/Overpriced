package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.presentation.mainnavigation.MainBottomNavigationScreenStateHolder

const val GroceryList = "groceryList"

fun NavController.navigateToGroceryListScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(GroceryList, builder)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
fun NavGraphBuilder.groceryListScreen(
    previousBackStackEntry: () -> NavBackStackEntry,
    mainBottomNavigationState: () -> MainBottomNavigationScreenStateHolder,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    lazyListState: LazyListState,
    navigateToEditGroceryList: (GroceryListId) -> Unit,
) {
    composable(GroceryList) {
        val groceryListScreenViewModel =
            hiltViewModel<GroceryListScreenViewModel>(previousBackStackEntry())

        GroceryListScreen(
            topBarScrollBehavior = topBarScrollBehavior,
            lazyListState = lazyListState,
            mainBottomNavigationState = mainBottomNavigationState(),
            groceryListScreenViewModel = groceryListScreenViewModel,
            navigateToEditGroceryList = navigateToEditGroceryList
        )
    }
}
