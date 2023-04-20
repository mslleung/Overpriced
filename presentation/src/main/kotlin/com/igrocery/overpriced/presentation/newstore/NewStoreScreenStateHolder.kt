package com.igrocery.overpriced.presentation.newstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng

class NewStoreScreenStateHolder(
    cameraPosition: LatLng,
    isSaveDialogShown: Boolean
) {

    var cameraPosition by mutableStateOf(cameraPosition)
    var isSaveDialogShown by mutableStateOf(isSaveDialogShown)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.cameraPosition,
                    it.isSaveDialogShown,
                )
            },
            restore = {
                NewStoreScreenStateHolder(
                    it[0] as LatLng,
                    it[1] as Boolean,
                )
            }
        )
    }
}

@Composable
fun rememberNewStoreScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(NewStoreScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(NewStoreScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(
        NewStoreScreenStateHolder(
            cameraPosition = LatLng(0.0, 0.0),
            isSaveDialogShown = false
        )
    )
}
