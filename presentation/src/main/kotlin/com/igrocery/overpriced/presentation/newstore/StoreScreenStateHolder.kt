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
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.MapProperties
import com.igrocery.overpriced.presentation.shared.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class StoreScreenStateHolder(context: Context) {

    var isInitialPermissionRequest by mutableStateOf(true)

    private val settingsClient = LocationServices.getSettingsClient(context)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geoCoder = Geocoder(context)

    private val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 3000
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }
    private val settingsRequestBuilder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)

    var mapProperties by mutableStateOf(
        MapProperties(
            isBuildingEnabled = true,
            isIndoorEnabled = true,
            isMyLocationEnabled = false,
            isTrafficEnabled = false
        )
    )

    enum class LiveLocationLoadState { Idle, Loading }

    var liveLocationLoadState by mutableStateOf(LiveLocationLoadState.Idle)
    var liveLocation by mutableStateOf<Location?>(null)
    var isFirstLocationUpdate by mutableStateOf(true)

    enum class GeocoderLoadState { Loading, Finished }

    var cameraPosition by mutableStateOf(LatLng(0.0, 0.0))
    var geocoderJob: Job? = null
    var geocoderLoadState by mutableStateOf(GeocoderLoadState.Loading)
    var address by mutableStateOf("")

//    var isConfirmDeleteDialogShown by mutableStateOf(false)
    var isSaveDialogShown by mutableStateOf(false)

//    var isRequestingFirstFocus by mutableStateOf(true)
//    var storeName by mutableStateOf("")
//    var dialogStoreAddress by mutableStateOf("")

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun checkLocationSettings(activity: Activity, onSuccess: () -> Unit) {
        settingsClient.checkLocationSettings(settingsRequestBuilder.build())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(activity, 0)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun tryUpdateLiveLocation(activity: Activity) {
        checkLocationSettings(activity) {
            liveLocationLoadState = LiveLocationLoadState.Loading

            if (liveLocation == null) {
                // first time load, use last location
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        liveLocation = location
                        liveLocationLoadState = LiveLocationLoadState.Idle
                    }
                    .addOnFailureListener {
                        updateCurrentLocation()
                    }
            } else {
                // subsequent loads, user is actively trying to locate himself/herself
                // use a more up-to-date position
                updateCurrentLocation()
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun updateCurrentLocation() {
        liveLocationLoadState = LiveLocationLoadState.Loading

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token // don't cancel
        )
            .addOnSuccessListener { location ->
                liveLocation = location
            }
            .addOnCompleteListener {
                liveLocationLoadState = LiveLocationLoadState.Idle
            }
    }

    suspend fun resolveAddress(latLng: LatLng) {
        withContext(Dispatchers.IO) {
            geocoderLoadState = GeocoderLoadState.Loading

            val lat = latLng.latitude.format(4)
            val lng = latLng.longitude.format(4)
            var resolvedAddress = "$lat,$lng"   // default

            runCatching {
                val resultAddresses =
                    geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)  // blocking
                if (resultAddresses != null && resultAddresses.isNotEmpty()) {
                    val resultAddress = resultAddresses[0]
                    var addressLines = ""
                    for (i in 0..resultAddress.maxAddressLineIndex) {
                        addressLines += resultAddress.getAddressLine(i)
                    }
                    resolvedAddress = addressLines
                }
            }

            if (isActive) {
                address = resolvedAddress
                geocoderLoadState = GeocoderLoadState.Finished
            }
        }
    }
}

@Composable
fun rememberStoreScreenState(context: Context) = rememberSaveable(
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
            StoreScreenStateHolder(context).apply {
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
    mutableStateOf(StoreScreenStateHolder(context))
}
