package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class EditGroceryListScreenStateHolder() {
    // placeholder...

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    false
                )
            },
            restore = {
                EditGroceryListScreenStateHolder()
            }
        )
    }
}

@Composable
fun rememberEditGroceryListScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(EditGroceryListScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(EditGroceryListScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(EditGroceryListScreenStateHolder())
}