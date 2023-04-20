package com.igrocery.overpriced.presentation.newstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class SaveAlertDialogStateHolder(
    storeName: String,
    address: String,
    isRequestingFirstFocus: Boolean,
) {
    var storeName by mutableStateOf(storeName)
    var address by mutableStateOf(address)
    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.storeName,
                    it.address,
                    it.isRequestingFirstFocus,
                )
            },
            restore = {
                SaveAlertDialogStateHolder(
                    it[0] as String,
                    it[1] as String,
                    it[2] as Boolean
                )
            }
        )
    }
}

@Composable
fun rememberSaveAlertDialogState(
    storeName: String,
    address: String,
) = rememberSaveable(
    stateSaver = Saver(
        save = { with(SaveAlertDialogStateHolder.Saver()) { save(it) } },
        restore = { value -> with(SaveAlertDialogStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(
        SaveAlertDialogStateHolder(
            storeName = storeName,
            address = address,
            isRequestingFirstFocus = true
        )
    )
}
