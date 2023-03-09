package com.igrocery.overpriced.presentation.shared

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.presentation.R

@Composable
fun ListSelectionDialog(
    selections: List<String>,
    onSelected: (index: Int) -> Unit,
) {

}

@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    messageText: String,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(
                    text = stringResource(id = R.string.confirm_delete_dialog_confirm_button_text),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(id = R.string.confirm_delete_dialog_dismiss_button_text))
            }
        },
        text = {
            Text(text = messageText)
        },
        modifier = modifier
    )
}
