package com.igrocery.overpriced.presentation.editstore

import android.Manifest
import android.location.Geocoder
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.igrocery.overpriced.domain.productpricehistory.models.Address
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.presentation.editstore.EditStoreScreenStateHolder.GeocoderLoadState
import com.igrocery.overpriced.presentation.editstore.EditStoreScreenViewModel.UpdateStoreResultState
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.ConfirmDeleteDialog
import com.igrocery.overpriced.presentation.shared.DeleteButton
import com.igrocery.overpriced.presentation.shared.SaveButton
import com.igrocery.overpriced.shared.Logger
import com.ireceipt.receiptscanner.presentation.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Suppress("unused")
private val log = Logger {}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
fun EditStoreScreen(
    storeId: Long,
    viewModel: EditStoreScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: () -> Unit,
) {
    log.debug("Composing EditStoreScreen")

    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.surface
    val navBarColor = MaterialTheme.colorScheme.surface
    SideEffect {
        systemUiController.setStatusBarColor(
            statusBarColor,
            transformColorForLightContent = { color -> color })
        systemUiController.setNavigationBarColor(
            navBarColor,
            navigationBarContrastEnforced = false,
            transformColorForLightContent = { color -> color })
    }

    LaunchedEffect(key1 = storeId) {
        viewModel.setStoreId(storeId)
    }

    val store by viewModel.storeFlow.collectAsState()
    val updateStoreResultState by viewModel.updateStoreResultStateFlow.collectAsState()
    val context = LocalContext.current
    val state by rememberEditStoreScreenState().apply {
        value.settingsClient = remember(context) { LocationServices.getSettingsClient(context) }
        value.fusedLocationClient =
            remember(context) { LocationServices.getFusedLocationProviderClient(context) }
        value.geoCoder = remember(context) { Geocoder(context) }
    }
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
            }
        }
    )

    val coroutineScope = rememberCoroutineScope()
    MainContent(
        store = store,
        updateStoreResultState = updateStoreResultState,
        state = state,
        onBackButtonClick = navigateUp,
        onDeleteButtonClick = {
            state.isConfirmDeleteDialogShown = true
        },
        onSaveButtonClick = {
            state.isSaveDialogShown = true
        },
        onMyLocationClick = { locationPermissionsState.launchMultiplePermissionRequest() },
        onCameraPositionChanged = {
            state.cameraPosition = it

            state.geocoderJob?.cancel()
            state.geocoderJob = coroutineScope.launch {
                state.resolveAddress(it)
            }
        }
    )

    if (state.isConfirmDeleteDialogShown) {
        ConfirmDeleteDialog(
            onDismiss = {
                state.isConfirmDeleteDialogShown = false
            },
            onConfirm = {
                state.isConfirmDeleteDialogShown = false
                navigateUp()

                assert(store != null)
                store?.let { viewModel.deleteStore(it) }
            },
            messageText = stringResource(id = R.string.store_delete_dialog_message)
        )
    }

    if (state.isSaveDialogShown) {
        val saveDialogState by rememberSaveAlertDialogState(
            initialStoreName = store?.name ?: "",
            initialAddress = state.address
        )
        SaveAlertDialog(
            state = saveDialogState,
            onDismiss = {
                state.isSaveDialogShown = false
            },
            onConfirm = {
                state.isSaveDialogShown = false
                viewModel.updateStore(
                    storeName = saveDialogState.storeName.trim(),
                    addressLines = saveDialogState.address.trim(),
                    latitude = state.cameraPosition.latitude,
                    longitude = state.cameraPosition.longitude
                )
            },
        )
    }

    if (updateStoreResultState is UpdateStoreResultState.Success) {
        LaunchedEffect(key1 = Unit) {
            navigateDone()
        }
    }

    BackHandler {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
private fun MainContent(
    store: Store?,
    updateStoreResultState: UpdateStoreResultState,
    state: EditStoreScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onMyLocationClick: () -> Unit,
    onCameraPositionChanged: (LatLng) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    if (updateStoreResultState is UpdateStoreResultState.Error) {
        val message = stringResource(id = R.string.edit_store_create_failed_message)
        LaunchedEffect(updateStoreResultState) {
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp)
                    )
                },
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = stringResource(id = R.string.edit_store_title))
                        Text(
                            text = store?.name ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                actions = {
                    DeleteButton(
                        onClick = onDeleteButtonClick,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp),
                        enabled = store != null
                    )
                    SaveButton(
                        onClick = onSaveButtonClick,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 10.dp),
                        enabled = state.geocoderLoadState == GeocoderLoadState.Finished
                    )
                },
                modifier = Modifier.statusBarsPadding()
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .navigationBarsPadding()
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
                store?.let { store ->
                    Marker(
                        state = MarkerState(
                            LatLng(
                                store.address.geoCoordinates.latitude,
                                store.address.geoCoordinates.longitude
                            )
                        )
                    )
                }
            }

            LaunchedEffect(key1 = Unit) {
                snapshotFlow { cameraPositionState.position }
                    .map { cameraPosition -> cameraPosition.target }
                    .collectLatest { latLng ->
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
                    loadState = state.geocoderLoadState,
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
                    onMyLocationClick = onMyLocationClick,
                    loadState = state.liveLocationLoadState,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(48.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                )
            }

            LaunchedEffect(store) {
                if (store != null) {
                    val newCameraPositionLatLng = LatLng(
                        store.address.geoCoordinates.latitude,
                        store.address.geoCoordinates.longitude
                    )
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(newCameraPositionLatLng, 16f)
                    )
                }
            }

            if (state.liveLocationLoadState == EditStoreScreenStateHolder.LiveLocationLoadState.Completed) {
                LaunchedEffect(Unit) {
                    state.liveLocation?.let { location ->
                        log.debug("animate ${location.latitude},${location.longitude}")
                        val newCameraPositionLatLng = LatLng(location.latitude, location.longitude)
                        val newZoom = cameraPositionState.position.zoom.coerceIn(16f, 20f)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(newCameraPositionLatLng, newZoom),
                            1000
                        )
                        state.liveLocationLoadState =
                            EditStoreScreenStateHolder.LiveLocationLoadState.Idle
                    }
                }
            }
        }
    }
}

@Composable
private fun GeocoderBox(
    loadState: GeocoderLoadState,
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
        when (loadState) {
            GeocoderLoadState.Loading -> {
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
            }
            GeocoderLoadState.Finished -> {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyLocationButton(
    onMyLocationClick: () -> Unit,
    loadState: EditStoreScreenStateHolder.LiveLocationLoadState,
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
        when (loadState) {
            EditStoreScreenStateHolder.LiveLocationLoadState.Idle,
            EditStoreScreenStateHolder.LiveLocationLoadState.Completed -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_my_location_24),
                    contentDescription = stringResource(id = R.string.store_map_my_location_button_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            EditStoreScreenStateHolder.LiveLocationLoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Preview
@Composable
private fun DefaultPreview() {
    MainContent(
        store = Store(
            name = "Walmart",
            address = Address("1 Awesome Lane, Hong Kong", GeoCoordinates(0.0, 0.0))
        ),
        updateStoreResultState = UpdateStoreResultState.Idle,
        state = EditStoreScreenStateHolder(),
        onBackButtonClick = {},
        onDeleteButtonClick = {},
        onSaveButtonClick = {},
        onMyLocationClick = {},
        onCameraPositionChanged = {},
    )
}
