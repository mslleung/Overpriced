package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class GroceryListNameDialogStateHolder(savedState: List<*>? = null) {

    var groceryListName by mutableStateOf(savedState?.get(0) as? String ?: "")

}

@Composable
fun rememberGroceryListNameDialogState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
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
