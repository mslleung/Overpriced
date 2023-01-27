package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val GroceryList = "groceryList"

fun NavController.navigateToGroceryListScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(GroceryList, builder)
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.groceryListScreen(
    topBarScrollBehavior: TopAppBarScrollBehavior,
    rootBackStackEntry: NavBackStackEntry,
    onFabVisibilityChanged: (Boolean) -> Unit,
    onCreateNewGroceryListClick: () -> Unit,
) {
    composable(GroceryList) {
        val groceryListScreenViewModel =
            hiltViewModel<GroceryListScreenViewModel>(rootBackStackEntry)

        GroceryListScreen(
            topBarScrollBehavior = topBarScrollBehavior,
            groceryListScreenViewModel = groceryListScreenViewModel,
            onFabVisibilityChanged = onFabVisibilityChanged,
            onCreateNewGroceryListClick = onCreateNewGroceryListClick
        )
    }
}
