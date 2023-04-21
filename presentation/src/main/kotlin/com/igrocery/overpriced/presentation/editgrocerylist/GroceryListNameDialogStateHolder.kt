package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.igrocery.overpriced.presentation.R

class GroceryListNameDialogStateHolder(
    isRequestingFirstFocus: Boolean,
    groceryListName: TextFieldValue,
) {

    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)
    var groceryListName by mutableStateOf(groceryListName)

    enum class ErrorState {
        None,
        ErrorNameCannotBeBlank
    }

    var errorState by mutableStateOf(ErrorState.None)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    with(TextFieldValue.Saver) { save(it.groceryListName) },
                )
            },
            restore = {
                GroceryListNameDialogStateHolder(
                    it[0] as Boolean,
                    with(TextFieldValue.Saver) { restore(it[1])!! },
                )
            }
        )
    }

}

@Composable
fun rememberGroceryListNameDialogState(
    initialName: String = stringResource(id = R.string.edit_grocery_list_default_name)
): MutableState<GroceryListNameDialogStateHolder> {
    return rememberSaveable(
        stateSaver = Saver(
            save = { with(GroceryListNameDialogStateHolder.Saver()) { save(it) } },
            restore = { value -> with(GroceryListNameDialogStateHolder.Saver()) { restore(value)!! } }
        )
    ) {
        mutableStateOf(
            GroceryListNameDialogStateHolder(
                isRequestingFirstFocus = true,
                groceryListName = TextFieldValue(
                    text = initialName,
                    selection = TextRange(0, initialName.length)
                )
            )
        )
    }
}
