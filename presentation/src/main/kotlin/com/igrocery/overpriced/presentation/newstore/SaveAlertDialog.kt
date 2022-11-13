package com.igrocery.overpriced.presentation.newstore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveAlertDialog(
    state: SaveAlertDialogStateHolder,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = state.storeName.isNotBlank() && state.address.isNotBlank()
            ) {
                Text(text = stringResource(id = R.string.store_save_dialog_confirm_button_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(id = R.string.store_save_dialog_dismiss_button_text))
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = state.storeName,
                    onValueChange = {
                        state.storeName = it.take(100)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = {
                        Text(text = stringResource(id = R.string.store_save_dialog_store_name_label))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                )

                if (state.isRequestingFirstFocus) {
                    state.isRequestingFirstFocus = false
                    LaunchedEffect(key1 = Unit) {
                        focusRequester.requestFocus()
                    }
                }

                OutlinedTextField(
                    value = state.address,
                    onValueChange = {
                        state.address = it.take(100)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text(text = stringResource(id = R.string.store_save_dialog_address_label))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                )
            }
        },
        modifier = modifier
    )
}