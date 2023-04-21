package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.parcelize.Parcelize

class EditGroceryListScreenStateHolder(
    isGroceryListNameDialogShown: Boolean
) {
    var isGroceryListNameDialogShown by mutableStateOf(isGroceryListNameDialogShown)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isGroceryListNameDialogShown
                )
            },
            restore = {
                EditGroceryListScreenStateHolder(
                    isGroceryListNameDialogShown = it[0]
                )
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
    mutableStateOf(
        EditGroceryListScreenStateHolder(
            isGroceryListNameDialogShown = false
        )
    )
}