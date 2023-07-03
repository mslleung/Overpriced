package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.igrocery.overpriced.domain.GroceryListId

private const val EditGroceryList = "editGroceryList"
private const val EditGroceryList_Arg_GroceryListId = "groceryListId"
private const val EditGroceryList_With_Args = "$EditGroceryList/{$EditGroceryList_Arg_GroceryListId}"

fun NavController.navigateToEditGroceryListScreen(
    groceryListId: GroceryListId,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    require(groceryListId.value > 0)
    navigate("$EditGroceryList/${groceryListId.value}", builder)
}

fun NavGraphBuilder.editGroceryListScreen(
    navigateUp: () -> Unit,
    navigateToSearchProduct: (query: String) -> Unit
) {
    composable(
        route = EditGroceryList_With_Args,
        arguments = listOf(navArgument(EditGroceryList_Arg_GroceryListId) {
            type = NavType.LongType
            defaultValue = 0L
        })
    ) { backStackEntry ->
        val editGroceryListScreenViewModel =
            hiltViewModel<EditGroceryListScreenViewModel>()

        val args = EditGroceryListScreenArgs(backStackEntry)

        EditGroceryListScreen(
            editGroceryListViewModel = editGroceryListScreenViewModel,
            navigateUp = navigateUp,
            navigateToSearchProduct = navigateToSearchProduct
        )
    }
}

internal data class EditGroceryListScreenArgs(
    val groceryListId: GroceryListId
) {
    constructor(backStackEntry: NavBackStackEntry) :
            this(
                groceryListId = backStackEntry.arguments?.getLong(EditGroceryList_Arg_GroceryListId)
                    .takeIf { it != 0L }
                    ?.let { GroceryListId(it) }
                    ?: throw IllegalArgumentException("groceryListId should not be null")
            )

    constructor(savedStateHandle: SavedStateHandle) :
            this(
                groceryListId = savedStateHandle.get<Long>(EditGroceryList_Arg_GroceryListId)
                    .takeIf { it != 0L }
                    ?.let { GroceryListId(it) }
                    ?: throw IllegalArgumentException("groceryListId should not be null")
            )
}
