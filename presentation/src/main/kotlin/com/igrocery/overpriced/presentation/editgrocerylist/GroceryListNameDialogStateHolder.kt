package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.presentation.R

class GroceryListNameDialogStateHolder(savedState: List<*>? = null) {

    var isRequestingFirstFocus by mutableStateOf(savedState?.get(0) as? Boolean ?: false)
    var groceryListName by mutableStateOf(savedState?.get(1) as? String ?: "")

}

@Composable
fun rememberGroceryListNameDialogState(): MutableState<GroceryListNameDialogStateHolder> {
    return rememberSaveable(
        stateSaver = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.groceryListName,
                )
            },
            restore = { savedState ->
                GroceryListNameDialogStateHolder(savedState)
            }
        )
    ) {
        mutableStateOf(GroceryListNameDialogStateHolder())
    }
}
