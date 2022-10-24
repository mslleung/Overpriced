package com.igrocery.overpriced.presentation.editstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng

class EditStoreScreenStateHolder(savedState: List<*>? = null) {

    var cameraPosition by mutableStateOf(savedState?.get(0) as? LatLng ?: LatLng(0.0, 0.0))
    var isConfirmDeleteDialogShown by mutableStateOf(savedState?.get(1) as? Boolean ?: false)
    var isSaveDialogShown by mutableStateOf(savedState?.get(2) as? Boolean ?: false)

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
        restore = { savedState ->
            EditStoreScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(EditStoreScreenStateHolder())
}
