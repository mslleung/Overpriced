package com.igrocery.overpriced.presentation.newgrocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class NewGroceryListScreenStateHolder(savedState: List<*>? = null) {
    // placeholder...
}

@Composable
fun rememberNewGroceryListScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                false
            )
        },
        restore = { savedState ->
            NewGroceryListScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(NewGroceryListScreenStateHolder())
}