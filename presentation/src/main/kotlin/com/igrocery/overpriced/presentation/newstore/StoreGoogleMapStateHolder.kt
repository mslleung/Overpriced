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
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.presentation.shared.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class StoreGoogleMapStateHolder(context: Context, val initialCameraPosition: GeoCoordinates? = null) {

    var initialCameraPlacementCompleted by mutableStateOf(false)

    private val settingsClient = LocationServices.getSettingsClient(context)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geoCoder = Geocoder(context)

    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

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

    var liveLocationLoadState: LoadingState<Location> by mutableStateOf(LoadingState.NotLoading())

    var geocoderJob: Job? = null
    var geocoderLoadState: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())
    var address by mutableStateOf("")

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
            when (liveLocationLoadState) {
                is LoadingState.NotLoading -> {
                    // first time load, use last location
                    liveLocationLoadState = LoadingState.Loading()

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            liveLocationLoadState = LoadingState.Success(location)
                        }
                        .addOnFailureListener {
                            // there is no last location, which is possible if the device is
                            // rebooted, we go ahead and fetch the current location
                            updateCurrentLocation()
                        }
                }
                else -> {
                    // subsequent loads, user is actively trying to locate himself/herself
                    // use a more up-to-date position
                    updateCurrentLocation()
                }
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun updateCurrentLocation() {
        liveLocationLoadState = LoadingState.Loading()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token // don't cancel
        )
            .addOnSuccessListener { location ->
                liveLocationLoadState = LoadingState.Success(location)
            }
            .addOnFailureListener {
                liveLocationLoadState = LoadingState.Error(it)
            }
    }

    suspend fun resolveAddress(latLng: LatLng) {
        withContext(Dispatchers.IO) {
            geocoderLoadState = LoadingState.Loading()

            val lat = latLng.latitude.format(4)
            val lng = latLng.longitude.format(4)
            var resolvedAddress = "$lat,$lng"   // default

            runCatching {
                // use blocking api so we can perform coroutine cancellation
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
                geocoderLoadState = LoadingState.NotLoading()
            }
        }
    }
}

@Composable
fun rememberStoreGoogleMapState(context: Context, initialCameraPosition: GeoCoordinates? = null) = rememberSaveable(
    context, initialCameraPosition,
    stateSaver = listSaver(
        save = {
            listOf(
                it.initialCameraPlacementCompleted,
                it.liveLocationLoadState,
            )
        },
        restore = {
            StoreGoogleMapStateHolder(context, initialCameraPosition).apply {
                initialCameraPlacementCompleted = it[0] as Boolean
                liveLocationLoadState = it[1] as LoadingState<Location>
            }
        }
    )
) {
    mutableStateOf(StoreGoogleMapStateHolder(context, initialCameraPosition))
}
