package com.igrocery.overpriced.presentation.editstore

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.newstore.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger

@Suppress("unused")
private val log = Logger {}

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

    val snackbarHostState = remember { SnackbarHostState() }
    val state by rememberEditStoreScreenState()
    val storeMapState by rememberStoreGoogleMapState(context = LocalContext.current)
    MainContent(
        viewModelState = viewModel.uiState,
        snackbarHostState = snackbarHostState,
        state = state,
        storeMapState = storeMapState,
        onCameraPositionChanged = {
            state.cameraPosition = it
        },
        onBackButtonClick = navigateUp,
        onDeleteButtonClick = {
            state.isConfirmDeleteDialogShown = true
        },
        onSaveButtonClick = {
            state.isSaveDialogShown = true
        }
    )

    if (state.isConfirmDeleteDialogShown) {
        val store = viewModel.uiState.store
        store.ifLoaded {
            ConfirmDeleteDialog(
                onDismiss = {
                    state.isConfirmDeleteDialogShown = false
                },
                onConfirm = {
                    state.isConfirmDeleteDialogShown = false
                    navigateUp()
                    viewModel.deleteStore(it)
                },
                messageText = stringResource(id = R.string.store_delete_dialog_message)
            )
        }
    }

    viewModel.uiState.deleteStoreResult.let {
        when (it) {
            is LoadingState.Success -> {
                LaunchedEffect(key1 = Unit) {
                    navigateDone()
                }
            }
            is LoadingState.Error -> {
                val message = stringResource(id = R.string.edit_store_delete_failed_message)
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

    if (state.isSaveDialogShown) {
        val store = viewModel.uiState.store
        store.ifLoaded {
            val saveDialogState by rememberSaveAlertDialogState(
                initialStoreName = it.name,
                initialAddress = storeMapState.address
            )
            SaveAlertDialog(
                state = saveDialogState,
                title = stringResource(id = R.string.edit_store_title),
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
    }

    viewModel.uiState.updateStoreResult.let {
        when (it) {
            is LoadingState.Success -> {
                LaunchedEffect(key1 = Unit) {
                    navigateDone()
                }
            }
            is LoadingState.Error -> {
                val message = stringResource(id = R.string.edit_store_update_failed_message)
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
    viewModelState: EditStoreScreenViewModel.ViewModelState,
    snackbarHostState: SnackbarHostState,
    state: EditStoreScreenStateHolder,
    storeMapState: StoreGoogleMapStateHolder,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onCameraPositionChanged: (LatLng) -> Unit
) {
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
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = stringResource(id = R.string.edit_store_title))

                        val store = viewModelState.store
                        store.ifLoaded {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                actions = {
                    val store = viewModelState.store
                    DeleteButton(
                        onClick = onDeleteButtonClick,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp),
                        enabled = store is LoadingState.Success
                    )
                    SaveButton(
                        onClick = onSaveButtonClick,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 10.dp),
                        enabled = storeMapState.geocoderLoadState is LoadingState.NotLoading<*>
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
            viewModelState.store.ifLoaded { store ->
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
    }
}

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Preview
@Composable
private fun DefaultPreview() {
    MainContent(
        viewModelState = EditStoreScreenViewModel.ViewModelState(),
        snackbarHostState = SnackbarHostState(),
        state = EditStoreScreenStateHolder(),
        storeMapState = StoreGoogleMapStateHolder(LocalContext.current),
        onBackButtonClick = {},
        onDeleteButtonClick = {},
        onSaveButtonClick = {},
        onCameraPositionChanged = {},
    )
}
