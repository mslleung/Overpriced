package com.igrocery.overpriced.presentation.newstore

import android.Manifest
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.LoadingState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    conditional = true
)
@Composable
fun StoreGoogleMap(
    state: StoreGoogleMapStateHolder,
    onCameraPositionChanged: (LatLng) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
        onPermissionsResult = { result ->
            val hasLocationPermission =
                result.getOrElse(Manifest.permission.ACCESS_COARSE_LOCATION) { false }
                        || result.getOrElse(Manifest.permission.ACCESS_FINE_LOCATION) { false }
            if (hasLocationPermission) {
                // has at least Manifest.permission.ACCESS_COARSE_LOCATION, can proceed to get location
                state.tryUpdateLiveLocation(activity)

                state.mapProperties = state.mapProperties.copy(
                    isMyLocationEnabled = true
                )
            } else {
                state.liveLocationLoadState = LoadingState.Error(
                    IllegalStateException("Insufficient permission for getting live location.")
                )
            }
        }
    )

    if (state.initialPermissionRequest) {
        LaunchedEffect(key1 = Unit) {
            locationPermissionsState.launchMultiplePermissionRequest()
            state.initialPermissionRequest = false
        }
    }

    Box(
        modifier = modifier
    ) {
        val cameraPositionState = rememberCameraPositionState {
            // initial default camera position
            val unitedStates = LatLng(37.0902, 95.7129)
            position = CameraPosition.fromLatLngZoom(unitedStates, 0f)
        }

        val context = LocalContext.current
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = state.mapProperties.copy(
                mapStyleOptions = if (isSystemInDarkTheme()) {
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style_night)
                } else {
                    null
                }
            ),
            uiSettings = MapUiSettings(
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,    // we provide a better-looking button ourselves
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false
            ),
            cameraPositionState = cameraPositionState
        ) {
            content()
        }

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(key1 = Unit) {
            snapshotFlow { cameraPositionState.position }
                .map { cameraPosition -> cameraPosition.target }
                .collectLatest { latLng ->
                    state.geocoderJob?.cancel()
                    state.geocoderJob = coroutineScope.launch {
                        state.resolveAddress(latLng)
                    }

                    onCameraPositionChanged(latLng)
                }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
        ) {
            GeocoderBox(
                isLoading = state.geocoderLoadState is LoadingState.Loading,
                address = state.address,
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_place_24),
                contentDescription = stringResource(id = R.string.store_map_selected_location_image_content_description),
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .size(48.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            MyLocationButton(
                onMyLocationClick = {
                    state.shouldMoveCamera = false
                    locationPermissionsState.launchMultiplePermissionRequest()
                },
                liveLocationLoadState = state.liveLocationLoadState,
                modifier = Modifier
                    .padding(12.dp)
                    .size(48.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
            )
        }

        LaunchedEffect(key1 = state.liveLocationLoadState) {
            state.liveLocationLoadState.let {
                if (it is LoadingState.Success) {
                    val location = it.data
                    val newCameraPositionLatLng = LatLng(location.latitude, location.longitude)
                    if (state.shouldMoveCamera) {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(newCameraPositionLatLng, 16f)
                        )
                        state.shouldMoveCamera = false
                    } else {
                        val newZoom = cameraPositionState.position.zoom.coerceIn(16f, 20f)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(newCameraPositionLatLng, newZoom),
                            1000
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeocoderBox(
    isLoading: Boolean,
    address: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .animateContentSize()
            .padding(8.dp), // for the shadow
        shape = RoundedCornerShape(20),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 12.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp, end = 4.dp),
                    strokeWidth = 2.dp
                )

                Text(
                    text = stringResource(id = R.string.store_loading_address),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        } else {
            Text(
                text = address,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun MyLocationButton(
    onMyLocationClick: () -> Unit,
    liveLocationLoadState: LoadingState<Location>,
    modifier: Modifier = Modifier
) {
    FilledTonalIconButton(
        onClick = onMyLocationClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        when (liveLocationLoadState) {
            is LoadingState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            }
            else -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_my_location_24),
                    contentDescription = stringResource(id = R.string.store_map_my_location_button_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
