package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class GroceryListScreenStateHolder(savedState: List<*>? = null) {
    // placeholder...
}

@Composable
fun rememberGroceryListScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                false
            )
        },
        restore = { savedState ->
            GroceryListScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(GroceryListScreenStateHolder())
}
