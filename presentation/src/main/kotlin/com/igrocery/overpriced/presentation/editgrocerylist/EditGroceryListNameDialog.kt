package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.presentation.R

@Composable
fun EditGroceryListNameDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(id = R.string.edit_grocery_list_name_dialog_confirm_button_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.edit_grocery_list_name_dialog_dismiss_button_text))
            }
        },
        text = {
            Text(
                text = stringResource(id = R.string.new_price_discard_changes_confirmation_dialog_message_text)
            )
        }
    )
}
