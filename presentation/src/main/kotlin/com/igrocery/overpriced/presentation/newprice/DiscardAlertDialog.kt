package com.igrocery.overpriced.presentation.newprice

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.presentation.R

@Composable
fun DiscardAlertDialog(
    onConfirmButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelButtonClick,
        confirmButton = {
            TextButton(
                onClick = onConfirmButtonClick
            ) {
                Text(text = stringResource(id = R.string.new_price_discard_changes_confirmation_dialog_confirm_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelButtonClick
            ) {
                Text(text = stringResource(id = R.string.new_price_discard_changes_confirmation_dialog_cancel_text))
            }
        },
        text = {
            Text(
                text = stringResource(id = R.string.new_price_discard_changes_confirmation_dialog_message_text)
            )
        }
    )
}
