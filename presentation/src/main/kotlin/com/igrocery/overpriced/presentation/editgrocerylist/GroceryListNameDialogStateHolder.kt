package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.presentation.R

class GroceryListNameDialogStateHolder(
    defaultGroceryListName: String,
    savedState: List<*>? = null
) {

    var isRequestingFirstFocus by mutableStateOf(savedState?.get(0) as? Boolean ?: true)
    var groceryListName by mutableStateOf(savedState?.get(1) as? String ?: defaultGroceryListName)

}

@Composable
fun rememberGroceryListNameDialogState(): MutableState<GroceryListNameDialogStateHolder> {
    val defaultGroceryListName =
        stringResource(id = R.string.grocery_lists_new_grocery_list_default_name)
    return rememberSaveable(
        stateSaver = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.groceryListName,
                )
            },
            restore = { savedState ->
                GroceryListNameDialogStateHolder(defaultGroceryListName, savedState)
            }
        )
    ) {
        mutableStateOf(GroceryListNameDialogStateHolder(defaultGroceryListName))
    }
}
