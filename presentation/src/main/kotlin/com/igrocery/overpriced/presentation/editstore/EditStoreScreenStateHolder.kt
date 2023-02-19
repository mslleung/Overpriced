package com.igrocery.overpriced.presentation.editstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng

class EditStoreScreenStateHolder(
    cameraPosition: LatLng,
    isConfirmDeleteDialogShown: Boolean,
    isSaveDialogShown: Boolean
) {

    var cameraPosition by mutableStateOf(cameraPosition)
    var isConfirmDeleteDialogShown by mutableStateOf(isConfirmDeleteDialogShown)
    var isSaveDialogShown by mutableStateOf(isSaveDialogShown)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.cameraPosition,
                    it.isConfirmDeleteDialogShown,
                    it.isSaveDialogShown,
                )
            },
            restore = {
                EditStoreScreenStateHolder(
                    it[0] as LatLng,
                    it[1] as Boolean,
                    it[2] as Boolean,
                )
            }
        )
    }
}

@Composable
fun rememberEditStoreScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(EditStoreScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(EditStoreScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(
        EditStoreScreenStateHolder(
            cameraPosition = LatLng(0.0, 0.0),
            isConfirmDeleteDialogShown = false,
            isSaveDialogShown = false
        )
    )
}
