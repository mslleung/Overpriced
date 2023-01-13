package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class EditGroceryListScreenStateHolder(savedState: List<*>? = null) {
    // placeholder...
}

@Composable
fun rememberEditGroceryListScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                false
            )
        },
        restore = { savedState ->
            EditGroceryListScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(EditGroceryListScreenStateHolder())
}