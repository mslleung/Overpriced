package com.igrocery.overpriced.presentation.editstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class SaveAlertDialogStateHolder(
    initialStoreName: String,
    initialAddress: String,
) {

    var storeName by mutableStateOf(initialStoreName)
    var address by mutableStateOf(initialAddress)
    var isRequestingFirstFocus by mutableStateOf(true)

    companion object {
        val Saver : Saver<SaveAlertDialogStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.storeName,
                    it.address,
                    it.isRequestingFirstFocus,
                )
            },
            restore = {
                SaveAlertDialogStateHolder(
                    initialStoreName = it[0] as String,
                    initialAddress = it[1] as String
                ).apply {
                    isRequestingFirstFocus = it[2] as Boolean
                }
            }
        )
    }
}

@Composable
fun rememberSaveAlertDialogState(
    initialStoreName: String,
    initialAddress: String,
) = rememberSaveable(
    stateSaver = SaveAlertDialogStateHolder.Saver
) {
    mutableStateOf(SaveAlertDialogStateHolder(initialStoreName, initialAddress))
}
