package com.igrocery.overpriced.presentation.editstore

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ireceipt.receiptscanner.presentation.R

@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(
                    text = stringResource(id = R.string.store_delete_dialog_confirm_button_text),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(id = R.string.store_delete_dialog_dismiss_button_text))
            }
        },
        text = {
            Text(text = stringResource(id = R.string.store_delete_dialog_message))
        },
        modifier = modifier
    )
}
