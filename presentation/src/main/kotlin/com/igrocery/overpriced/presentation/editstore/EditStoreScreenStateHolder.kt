package com.igrocery.overpriced.presentation.editstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng

class EditStoreScreenStateHolder {
    var cameraPosition by mutableStateOf(LatLng(0.0, 0.0))
    var isConfirmDeleteDialogShown by mutableStateOf(false)
    var isSaveDialogShown by mutableStateOf(false)
}

@Composable
fun rememberEditStoreScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.cameraPosition,
                it.isConfirmDeleteDialogShown,
                it.isSaveDialogShown,
            )
        },
        restore = {
            EditStoreScreenStateHolder().apply {
                cameraPosition = it[0] as LatLng
                isConfirmDeleteDialogShown = it[1] as Boolean
                isSaveDialogShown = it[2] as Boolean
            }
        }
    )
) {
    mutableStateOf(EditStoreScreenStateHolder())
}
