package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.igrocery.overpriced.presentation.R

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
                onClick = onConfirm
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
            OutlinedTextField(
                value = state.groceryListName,
                onValueChange = {
                    state.groceryListName = it.take(100)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.grocery_list_name_dialog_name_input_label_text))
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
            )
        },
        modifier = modifier
    )
}
