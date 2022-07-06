package com.igrocery.overpriced.presentation.editstore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class EditStoreScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val storeService: StoreService,
) : ViewModel() {

    private companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val storeFlow = savedStateHandle.getStateFlow(KEY_STORE_ID, 0L)
        .flatMapLatest { storeService.getStoreById(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun setStoreId(storeId: Long) {
        savedStateHandle[KEY_STORE_ID] = storeId
    }

    open class UpdateStoreResultState private constructor() {
        object Idle : UpdateStoreResultState()
        object Success : UpdateStoreResultState()
        object Error : UpdateStoreResultState()
    }

    private val _updateStoreResultStateFlow = MutableStateFlow<UpdateStoreResultState>(UpdateStoreResultState.Idle)
    val updateStoreResultStateFlow: StateFlow<UpdateStoreResultState> = _updateStoreResultStateFlow

    fun updateStore(
        storeId: Long,
        storeName: String,
        addressLines: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            with(_updateStoreResultStateFlow) {
                try {
                    emit(UpdateStoreResultState.Idle)

                    storeService.updateStore(storeId, storeName, addressLines, latitude, longitude)

                    emit(UpdateStoreResultState.Success)
                } catch (e: Exception) {
                    log.error(e.toString())
                    emit(UpdateStoreResultState.Error)
                }
            }
        }
    }

    fun deleteStore(store: Store) {
        viewModelScope.launch {
            storeService.deleteStore(store)
        }
    }

}
