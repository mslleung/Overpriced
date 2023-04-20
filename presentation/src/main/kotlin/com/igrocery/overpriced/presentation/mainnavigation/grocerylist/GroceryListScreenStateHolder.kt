package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class GroceryListScreenStateHolder() {
    // placeholder...

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    false
                )
            },
            restore = {
                GroceryListScreenStateHolder()
            }
        )
    }
}

@Composable
fun rememberGroceryListScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(GroceryListScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(GroceryListScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(GroceryListScreenStateHolder())
}
