package com.igrocery.overpriced.presentation.newstore

import android.Manifest
import android.location.Geocoder
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.igrocery.overpriced.presentation.newstore.NewStoreScreenStateHolder.GeocoderLoadState
import com.igrocery.overpriced.presentation.newstore.NewStoreScreenViewModel.CreateStoreResultState
import com.igrocery.overpriced.presentation.shared.BackButton
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
fun NewStoreScreen(
    newStoreViewModel: NewStoreScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: (newStoreId: Long) -> Unit,
) {
    log.debug("Composing NewStoreScreen")

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

    val createStoreResultState by newStoreViewModel.createStoreResultStateFlow.collectAsState()
    val context = LocalContext.current
    val state by rememberNewStoreScreenState().apply {
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

    if (state.isFirstLocationUpdate) {
        LaunchedEffect(key1 = Unit) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    val coroutineScope = rememberCoroutineScope()
    MainContent(
        createStoreResultState = createStoreResultState,
        state = state,
        onBackButtonClick = navigateUp,
        onSaveButtonClick = {
            state.isRequestingFirstFocus = true
            state.dialogStoreAddress = state.address
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

    if (state.isSaveDialogShown) {
        SaveAlertDialog(
            storeName = { state.storeName },
            onStoreNameChange = { text ->
                if (text.length > 100) {
                    state.storeName = text.substring(0, 100)
                } else {
                    state.storeName = text
                }
            },
            address = state.dialogStoreAddress,
            onAddressChange = { text ->
                if (text.length > 100) {
                    state.dialogStoreAddress = text.substring(0, 100)
                } else {
                    state.dialogStoreAddress = text
                }
            },
            onDismiss = {
                state.isSaveDialogShown = false
            },
            onConfirm = {
                state.isSaveDialogShown = false
                newStoreViewModel.createStore(
                    storeName = state.storeName.trim(),
                    addressLines = state.dialogStoreAddress.trim(),
                    latitude = state.cameraPosition.latitude,
                    longitude = state.cameraPosition.longitude
                )
            },
            requestFocus = state.isRequestingFirstFocus,
            onFocusRequested = { state.isRequestingFirstFocus = false }
        )
    }

    if (createStoreResultState is CreateStoreResultState.Success) {
        LaunchedEffect(key1 = Unit) {
            navigateDone((createStoreResultState as CreateStoreResultState.Success).id)
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
    createStoreResultState: CreateStoreResultState?,
    state: NewStoreScreenStateHolder,
    onBackButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onMyLocationClick: () -> Unit,
    onCameraPositionChanged: (LatLng) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    if (createStoreResultState is CreateStoreResultState.Error) {
        val message = stringResource(id = R.string.new_store_create_failed_message)
        LaunchedEffect(createStoreResultState) {
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
                    Text(text = stringResource(id = R.string.new_store_title))
                },
                actions = {
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

            LaunchedEffect(key1 = state.liveLocationLoadState) {
                state.liveLocation?.let { location ->
                    val newCameraPositionLatLng = LatLng(location.latitude, location.longitude)
                    if (state.isFirstLocationUpdate) {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(newCameraPositionLatLng, 16f)
                        )
                        state.isFirstLocationUpdate = false
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
    loadState: NewStoreScreenStateHolder.LiveLocationLoadState,
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
            NewStoreScreenStateHolder.LiveLocationLoadState.Idle -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_my_location_24),
                    contentDescription = stringResource(id = R.string.store_map_my_location_button_content_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            NewStoreScreenStateHolder.LiveLocationLoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SaveAlertDialog(
    storeName: () -> String,
    onStoreNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    requestFocus: Boolean = true,
    onFocusRequested: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = storeName().isNotBlank() && address.isNotBlank()
            ) {
                Text(text = stringResource(id = R.string.store_save_dialog_confirm_button_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(text = stringResource(id = R.string.store_save_dialog_dismiss_button_text))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.new_store_title))
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = storeName(),
                    onValueChange = { onStoreNameChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    label = {
                        Text(text = stringResource(id = R.string.store_save_dialog_store_name_label))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                )
                if (requestFocus) {
                    onFocusRequested()
                    LaunchedEffect(key1 = Unit) {
                        focusRequester.requestFocus()
                    }
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { onAddressChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text(text = stringResource(id = R.string.store_save_dialog_address_label))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                )
            }
        },
        modifier = modifier
    )
}

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Preview
@Composable
private fun DefaultPreview() {
    MainContent(
        createStoreResultState = null,
        state = NewStoreScreenStateHolder(),
        onBackButtonClick = {},
        onSaveButtonClick = {},
        onMyLocationClick = {},
        onCameraPositionChanged = {},
    )
}
