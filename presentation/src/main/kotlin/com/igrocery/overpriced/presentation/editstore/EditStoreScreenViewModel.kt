package com.igrocery.overpriced.presentation.editstore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.models.Address
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.presentation.NavDestinations
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class EditStoreScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storeService: StoreService,
) : ViewModel() {

    private val storeId = savedStateHandle.get<Long>(NavDestinations.EditStore_Arg_StoreId) ?: 0L

    class ViewModelState {
        var storeFlow: StateFlow<LoadingState<Store>> by mutableStateOf(
            MutableStateFlow(LoadingState.Loading())
        )
        var updateStoreResult: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())
        var deleteStoreResult: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            storeFlow = storeService.getStoreById(storeId)
                .map {
                    if (it == null) {
                        LoadingState.Error(Exception("Store not found"))
                    } else {
                        LoadingState.Success(it)
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadingState.Loading()
                )
        }
    }

    fun updateStore(
        storeName: String,
        addressLines: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            with(uiState) {
                runCatching {
                    updateStoreResult = LoadingState.Loading()
                    val originalStore = storeFlow.first()
                    if (originalStore is LoadingState.Success) {
                        val updatedStore = originalStore.data.copy(
                            name = storeName,
                            address = Address(
                                lines = addressLines,
                                geoCoordinates = GeoCoordinates(latitude, longitude)
                            )
                        )

                        storeService.updateStore(updatedStore)

                        updateStoreResult = LoadingState.Success(Unit)
                    } else {
                        throw IllegalStateException("Store is not loaded.")
                    }
                }.onFailure {
                    updateStoreResult = LoadingState.Error(it)
                }
            }
        }
    }

    fun deleteStore(store: Store) {
        viewModelScope.launch {
            with(uiState) {
                runCatching {
                    deleteStoreResult = LoadingState.Loading()
                    storeService.deleteStore(store)
                    deleteStoreResult = LoadingState.Success(Unit)
                }.onFailure {
                    deleteStoreResult = LoadingState.Error(it)
                }
            }
        }
    }

}
