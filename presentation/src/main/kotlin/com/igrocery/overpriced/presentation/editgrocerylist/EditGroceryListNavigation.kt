package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.presentation.editcategory.*

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

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.editGroceryListScreen(
    navigateUp: () -> Unit,
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

        val args = EditCategoryScreenArgs(backStackEntry)

        EditGroceryListScreen(
            editGroceryListViewModel = editGroceryListScreenViewModel,
            navigateUp = navigateUp,
        )
    }
}

internal class EditGroceryListScreenArgs(
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
