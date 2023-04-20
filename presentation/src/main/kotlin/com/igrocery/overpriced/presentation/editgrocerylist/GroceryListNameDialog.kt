package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.editgrocerylist.GroceryListNameDialogStateHolder.ErrorState
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger { }

@Composable
fun NewGroceryListNameDialog(
    state: GroceryListNameDialogStateHolder,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(id = R.string.grocery_list_name_dialog_new_title_text)
    GroceryListNameDialog(state, title, onConfirm, onDismiss, modifier)
}

@Composable
fun EditGroceryListNameDialog(
    state: GroceryListNameDialogStateHolder,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(id = R.string.grocery_list_name_dialog_edit_title_text)
    GroceryListNameDialog(state, title, onConfirm, onDismiss, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroceryListNameDialog(
    state: GroceryListNameDialogStateHolder,
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = state.groceryListName.text.isNotBlank()
            ) {
                Text(text = stringResource(id = R.string.grocery_list_name_dialog_confirm_button_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.grocery_list_name_dialog_dismiss_button_text))
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = state.groceryListName,
                    onValueChange = {
                        state.groceryListName = it.copy(text = it.text.take(100))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = {
                        Text(text = stringResource(id = R.string.grocery_list_name_dialog_name_input_label_text))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    isError = state.errorState != ErrorState.None
                )

                if (state.isRequestingFirstFocus) {
                    state.isRequestingFirstFocus = false
                    LaunchedEffect(key1 = Unit) {
                        focusRequester.requestFocus()
                    }
                }
            }
        },
        modifier = modifier
    )
}
