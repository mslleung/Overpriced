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
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface EditStoreScreenViewModelState {
    val storeFlow: StateFlow<LoadingState<Store>>
    val updateStoreResult: LoadingState<Unit>
    val deleteStoreResult: LoadingState<Unit>
}

@HiltViewModel
class EditStoreScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storeService: StoreService,
) : ViewModel(), EditStoreScreenViewModelState {

    private val args = EditStoreScreenArgs(savedStateHandle)

    override val storeFlow = storeService.getStore(args.storeId)
        .map { LoadingState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    override var updateStoreResult: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())

    override var deleteStoreResult: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())

    fun updateStore(
        storeName: String,
        addressLines: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            runCatching {
                updateStoreResult = LoadingState.Loading()
                val originalStore = storeFlow.value
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

    fun deleteStore(store: Store) {
        viewModelScope.launch {
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
