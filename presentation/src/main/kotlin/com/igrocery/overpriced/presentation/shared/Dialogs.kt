package com.igrocery.overpriced.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.igrocery.overpriced.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSelectionDialog(
    selections: List<String>,
    onDismiss: () -> Unit,
    onSelected: (index: Int) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                selections.forEachIndexed { index, selection ->
                    TextButton(
                        onClick = { onSelected(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = selection,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
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
