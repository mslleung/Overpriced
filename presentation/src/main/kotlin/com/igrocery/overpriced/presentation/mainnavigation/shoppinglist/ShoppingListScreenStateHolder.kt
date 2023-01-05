package com.igrocery.overpriced.presentation.mainnavigation.shoppinglist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class ShoppingListScreenStateHolder(savedState: List<*>? = null) {
    // placeholder...
}

@Composable
fun rememberShoppingListScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                false
            )
        },
        restore = { savedState ->
            ShoppingListScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(ShoppingListScreenStateHolder())
}
