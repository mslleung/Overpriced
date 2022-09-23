package com.igrocery.overpriced.presentation.newstore

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Geocoder
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.MapProperties
import com.igrocery.overpriced.presentation.shared.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class NewStoreScreenStateHolder(context: Context) {

    var cameraPosition by mutableStateOf(LatLng(0.0, 0.0))

    var isInitialPermissionRequest by mutableStateOf(true)

    //    var isConfirmDeleteDialogShown by mutableStateOf(false)
    var isSaveDialogShown by mutableStateOf(false)

//    var isRequestingFirstFocus by mutableStateOf(true)
//    var storeName by mutableStateOf("")
//    var dialogStoreAddress by mutableStateOf("")

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