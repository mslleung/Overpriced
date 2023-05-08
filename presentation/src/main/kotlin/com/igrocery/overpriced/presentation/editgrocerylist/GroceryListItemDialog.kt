package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.presentation.editgrocerylist.GroceryListItemDialogStateHolder.ErrorState

@Suppress("unused")
private val log = Logger { }

@Composable
fun GroceryListItemDialog(
    state: GroceryListItemDialogStateHolder,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(text = stringResource(id = R.string.add_grocery_list_item_dialog_confirm_button_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.add_grocery_list_item_dialog_dismiss_button_text))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.add_grocery_list_item_dialog_title))
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ItemNameField(
                    itemName = state.itemName,
                    onItemNameChange = {
                        state.itemName = it.copy(text = it.text.take(100))
                    },
                    error = state.errorState,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                ItemDescriptionField(
                    itemDescription = state.itemDescription,
                    onItemDescriptionChange = {
                        state.itemDescription = it.take(100)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ItemNameField(
    itemName: TextFieldValue,
    onItemNameChange: (TextFieldValue) -> Unit,
    error: ErrorState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    OutlinedTextField(
        value = itemName,
        onValueChange = onItemNameChange,
        modifier = modifier
            .focusRequester(focusRequester),
        singleLine = true,
        label = {
            Text(text = stringResource(id = R.string.add_grocery_list_item_dialog_item_name))
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        }),
        isError = error == ErrorState.ErrorNameCannotBeBlank
    )

    AnimatedVisibility(visible = error == ErrorState.ErrorNameCannotBeBlank) {
        Text(
            text = stringResource(id = R.string.add_grocery_list_item_dialog_item_name_empty_error),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }

    var isRequestingFirstFocus by rememberSaveable { mutableStateOf(true) }
    if (isRequestingFirstFocus) {
        isRequestingFirstFocus = false
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun ItemDescriptionField(
    itemDescription: String,
    onItemDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = itemDescription,
        onValueChange = onItemDescriptionChange,
        modifier = modifier,
        singleLine = true,
        label = {
            Text(text = stringResource(id = R.string.add_grocery_list_item_dialog_item_description))
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
    )
}
