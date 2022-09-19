package com.igrocery.overpriced.presentation.newstore

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class NewStoreScreenStateHolder {
}

@Composable
fun rememberNewStoreScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.liveLocation,
                it.isFirstLocationUpdate,
                it.isSaveDialogShown,
                it.isRequestingFirstFocus,
                it.storeName,
                it.dialogStoreAddress,
            )
        },
        restore = {
            NewStoreScreenStateHolder(context).apply {
                liveLocation = it[0] as Location?
                isFirstLocationUpdate = it[1] as Boolean
                isSaveDialogShown = it[2] as Boolean
                isRequestingFirstFocus = it[3] as Boolean
                storeName = it[4] as String
                dialogStoreAddress = it[5] as String
            }
        }
    )
) {

}