package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class GroceryListItemDialogStateHolder(
    itemName: TextFieldValue,
    itemDescription: String
) {

    var itemName by mutableStateOf(itemName)
    var itemDescription by mutableStateOf(itemDescription)

    enum class ErrorState {
        None,
        ErrorNameCannotBeBlank
    }

    var errorState by mutableStateOf(ErrorState.None)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    with(TextFieldValue.Saver) { save(it.itemName) },
                    it.itemDescription
                )
            },
            restore = {
                GroceryListItemDialogStateHolder(
                    with(TextFieldValue.Saver) { restore(it[0])!! },
                    it[1] as String
                )
            }
        )
    }

}

@Composable
fun rememberGroceryListItemDialogState(
    initialName: String = "",
    initialDescription: String = ""
): MutableState<GroceryListItemDialogStateHolder> {
    return rememberSaveable(
        stateSaver = Saver(
            save = { with(GroceryListItemDialogStateHolder.Saver()) { save(it) } },
            restore = { value -> with(GroceryListItemDialogStateHolder.Saver()) { restore(value)!! } }
        )
    ) {
        mutableStateOf(
            GroceryListItemDialogStateHolder(
                itemName = TextFieldValue(
                    text = initialName,
                    selection = TextRange(0, initialName.length)
                ),
                itemDescription = initialDescription
            )
        )
    }
}
