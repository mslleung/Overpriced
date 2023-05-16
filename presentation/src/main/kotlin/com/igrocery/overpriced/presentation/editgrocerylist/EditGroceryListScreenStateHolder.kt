package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem

class EditGroceryListScreenStateHolder(
    isGroceryListNameDialogShown: Boolean,
    isAddGroceryListItemDialogShown: Boolean,
    editingGroceryListItem: GroceryListItem?,
    longClickGroceryListItem: GroceryListItem?,
    isConfirmDeleteDialogShown: Boolean
) {
    var isGroceryListNameDialogShown by mutableStateOf(isGroceryListNameDialogShown)
    var isAddGroceryListItemDialogShown by mutableStateOf(isAddGroceryListItemDialogShown)
    var editingGroceryListItem by mutableStateOf(editingGroceryListItem)
    var longClickGroceryListItem by mutableStateOf(longClickGroceryListItem)
    var isConfirmDeleteDialogShown by mutableStateOf(isConfirmDeleteDialogShown)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isGroceryListNameDialogShown,
                    it.isAddGroceryListItemDialogShown,
                    it.editingGroceryListItem,
                    it.longClickGroceryListItem,
                    it.isConfirmDeleteDialogShown
                )
            },
            restore = {
                EditGroceryListScreenStateHolder(
                    isGroceryListNameDialogShown = it[0],
                    isAddGroceryListItemDialogShown = it[1],
                    editingGroceryListItem = it[2] as? GroceryListItem,
                    longClickGroceryListItem = it[3] as? GroceryListItem,
                    isConfirmDeleteDialogShown = it[4]
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
            isGroceryListNameDialogShown = false,
            isAddGroceryListItemDialogShown = false,
            editingGroceryListItem = null,
            longClickGroceryListItem = null,
            isConfirmDeleteDialogShown = false
        )
    )
}
