package com.igrocery.overpriced.presentation.newstore

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger {}

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    conditional = true
)
@Composable
fun NewStoreScreen(
    newStoreViewModel: NewStoreScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: (newStoreId: StoreId) -> Unit,
) {
    log.debug("Composing NewStoreScreen")

    val snackbarHostState = remember { SnackbarHostState() }
    val state by rememberNewStoreScreenState()
    val storeMapState by rememberStoreGoogleMapState(context = LocalContext.current)
    MainContent(
        snackbarHostState = snackbarHostState,
        state = state,
        storeMapState = storeMapState,
        onCameraPositionChanged = {
            state.cameraPosition = it
        },
        onBackButtonClick = navigateUp,
        onSaveButtonClick = {
            state.isSaveDialogShown = true
        }
    )

    if (state.isSaveDialogShown) {
        val saveDialogState by rememberSaveAlertDialogState(
            storeName = "",
            address = storeMapState.address
        )
        SaveAlertDialog(
            state = saveDialogState,
            title = stringResource(id = R.string.new_store_title),
            onDismiss = {
                state.isSaveDialogShown = false
            },
            onConfirm = {
                state.isSaveDialogShown = false
                newStoreViewModel.createStore(
                    storeName = saveDialogState.storeName.trim(),
                    addressLines = saveDialogState.address.trim(),
                    latitude = state.cameraPosition.latitude,
                    longitude = state.cameraPosition.longitude
                )
            },
        )
    }

    newStoreViewModel.createStoreResultState.let {
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
        log.debug("NewStoreScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
private fun MainContent(
    snackbarHostState: SnackbarHostState,
    state: NewStoreScreenStateHolder,
    storeMapState: StoreGoogleMapStateHolder,
    onCameraPositionChanged: (LatLng) -> Unit,
    onBackButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    UseDefaultStatusBarColor()
    UseDefaultBottomNavBarColourForSystemNavBarColor()

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
                        enabled = storeMapState.geocoderLoadState is LoadingState.NotLoading
                    )
                },
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
        ) {
            // nothing...
        }
    }
}

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    conditional = true
)
@Preview
@Composable
private fun DefaultPreview() {
    val state by rememberNewStoreScreenState()
    MainContent(
        snackbarHostState = SnackbarHostState(),
        state = state,
        storeMapState = StoreGoogleMapStateHolder(LocalContext.current),
        onCameraPositionChanged = { },
        onBackButtonClick = { },
        onSaveButtonClick = { }
    )
}
