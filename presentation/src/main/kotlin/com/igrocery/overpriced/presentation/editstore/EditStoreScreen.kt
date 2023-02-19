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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.igrocery.overpriced.domain.productpricehistory.models.Address
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.presentation.R
import com.igrocery.overpriced.presentation.newstore.*
import com.igrocery.overpriced.presentation.shared.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused")
private val log = Logger {}

@Composable
fun EditStoreScreen(
    viewModel: EditStoreScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: () -> Unit,
) {
    log.debug("Composing EditStoreScreen")

    val snackbarHostState = remember { SnackbarHostState() }
    val state by rememberEditStoreScreenState()
    val storeState by viewModel.storeFlow.collectAsState()
    storeState.ifLoaded { store ->
        val storeMapState by rememberStoreGoogleMapState(
            context = LocalContext.current,
            initialCameraPosition = store.address.geoCoordinates
        )
        MainContent(
            viewModelState = viewModel,
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
            ConfirmDeleteDialog(
                onDismiss = {
                    state.isConfirmDeleteDialogShown = false
                },
                onConfirm = {
                    state.isConfirmDeleteDialogShown = false
                    navigateUp()
                    viewModel.deleteStore(store)
                },
                messageText = stringResource(id = R.string.store_delete_dialog_message)
            )
        }

        viewModel.deleteStoreResult.let {
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
            val saveDialogState by rememberSaveAlertDialogState(
                storeName = store.name,
                address = storeMapState.address
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

        viewModel.updateStoreResult.let {
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
    }

    BackHandler {
        log.debug("EditStoreScreen: BackHandler")
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModelState: EditStoreScreenViewModelState,
    snackbarHostState: SnackbarHostState,
    state: EditStoreScreenStateHolder,
    storeMapState: StoreGoogleMapStateHolder,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onCameraPositionChanged: (LatLng) -> Unit
) {
    UseDefaultStatusBarColor()
    UseDefaultSystemNavBarColor()

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

                        val store by viewModelState.storeFlow.collectAsState()
                        store.ifLoaded {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                actions = {
                    val store by viewModelState.storeFlow.collectAsState()
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
            val storeState by viewModelState.storeFlow.collectAsState()
            storeState.ifLoaded { store ->
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
    val viewModelState = object : EditStoreScreenViewModelState {
        override val storeFlow: StateFlow<LoadingState<Store>>
            get() = MutableStateFlow(
                LoadingState.Success(
                    Store(
                        name = "Welcome",
                        address = Address(
                            lines = "100 Street",
                            geoCoordinates = GeoCoordinates(0.0, 0.0)
                        )
                    )
                )
            )
        override val updateStoreResult: LoadingState<Unit>
            get() = LoadingState.NotLoading()
        override val deleteStoreResult: LoadingState<Unit>
            get() = LoadingState.NotLoading()
    }

    val state by rememberEditStoreScreenState()

    MainContent(
        viewModelState = viewModelState,
        snackbarHostState = SnackbarHostState(),
        state = state,
        storeMapState = StoreGoogleMapStateHolder(LocalContext.current),
        onBackButtonClick = {},
        onDeleteButtonClick = {},
        onSaveButtonClick = {},
        onCameraPositionChanged = {},
    )
}
