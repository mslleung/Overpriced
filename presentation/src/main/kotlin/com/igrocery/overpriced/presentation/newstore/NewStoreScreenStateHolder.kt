package com.igrocery.overpriced.presentation.newstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng

class NewStoreScreenStateHolder {
    var cameraPosition by mutableStateOf(LatLng(0.0, 0.0))
    var isSaveDialogShown by mutableStateOf(false)
}

@Composable
fun rememberNewStoreScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.cameraPosition,
                it.isSaveDialogShown,
            )
        },
        restore = {
            NewStoreScreenStateHolder().apply {
                cameraPosition = it[0] as LatLng
                isSaveDialogShown = it[1] as Boolean
            }
        }
    )
) {
    mutableStateOf(NewStoreScreenStateHolder())
}
