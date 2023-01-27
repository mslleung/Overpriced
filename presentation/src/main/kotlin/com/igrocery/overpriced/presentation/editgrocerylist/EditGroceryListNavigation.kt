package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.igrocery.overpriced.domain.GroceryListId

private const val EditGroceryList = "editGroceryList"
private const val EditGroceryList_Arg_GroceryListId = "groceryListId"
private const val EditGroceryList_With_Args = "$EditGroceryList/{$EditGroceryList_Arg_GroceryListId}"

fun NavController.navigateToEditGroceryListScreen(
    groceryListId: GroceryListId,
    navOptions: NavOptions? = null
) {
    require(categoryId.value > 0)
    navigate("$EditCategory/$categoryId", navOptions)
}