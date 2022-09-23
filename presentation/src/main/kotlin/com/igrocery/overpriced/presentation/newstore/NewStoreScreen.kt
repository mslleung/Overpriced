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
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.editstore.EditStoreScreenViewModel
import com.igrocery.overpriced.presentation.editstore.rememberEditStoreScreenState
import com.igrocery.overpriced.presentation.editstore.rememberSaveAlertDialogState
import com.igrocery.overpriced.presentation.newstore.NewStoreScreenStateHolder.GeocoderLoadState
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.presentation.shared.ConfirmDeleteDialog
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.presentation.shared.SaveButton
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Suppress("unused")
private val log = Logger {}

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

    val snackbarHostState = remember { SnackbarHostState() }
    val state by rememberNewStoreScreenState()
    MainContent(
        snackbarHostState = snackbarHostState,
        state = state,
        onCameraPositionChanged = {
            state.cameraPosition = it
        },
        onBackButtonClick = navigateUp,
        onSaveButtonClick = {
            state.isRequestingFirstFocus = true
            state.dialogStoreAddress = state.address
            state.isSaveDialogShown = true
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

    newStoreViewModel.uiState.createStoreResultState.let {
        when (it) {
            is LoadingState.Success -> {
                LaunchedEffect(key1 = Unit) {
                    navigateDone(it.data)
                }
            }
            is LoadingState.Error -> {
                val message = stringResource(id = R.string.new_store_create_failed_message)
                LaunchedEffect(it) {
                    snackbarHostState.showSnackbar(
                        message = message,
                        withDismissAction = true
                    )
                }
            }
            else -> {}
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
    snackbarHostState: SnackbarHostState,
    state: NewStoreScreenStateHolder,
    onCameraPositionChanged: (LatLng) -> Unit,
    onBackButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    val storeMapState by rememberStoreGoogleMapState(context = LocalContext.current)
    Scaffold(
        topBar = {
            TopAppBar(
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
                        enabled = storeMapState.geocoderLoadState == StoreGoogleMapStateHolder.GeocoderLoadState.Finished
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
        StoreGoogleMap(
            state = storeMapState,
            onCameraPositionChanged = onCameraPositionChanged,
            modifier = Modifier
                .padding(it)
                .navigationBarsPadding()
        ) {
            // nothing...
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        snackbarHostState = SnackbarHostState(),
        state = NewStoreScreenStateHolder(LocalContext.current),
        onBackButtonClick = {},
        onSaveButtonClick = {},
        onMyLocationClick = {},
        onCameraPositionChanged = {},
    )
}
