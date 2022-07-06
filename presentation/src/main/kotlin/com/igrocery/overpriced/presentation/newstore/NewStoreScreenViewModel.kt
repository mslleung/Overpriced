package com.igrocery.overpriced.presentation.newstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class NewStoreScreenViewModel @Inject constructor(
    private val storeService: StoreService
) : ViewModel() {

    open class CreateStoreResultState private constructor() {
        class Success(val id: Long) :CreateStoreResultState()
        class Error: CreateStoreResultState()
    }

    private val _createStoreResultStateFlow = MutableStateFlow<CreateStoreResultState?>(null)
    val createStoreResultStateFlow: StateFlow<CreateStoreResultState?> = _createStoreResultStateFlow

    fun createStore(
        storeName: String,
        addressLines: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            with (_createStoreResultStateFlow) {
                try {
                    emit(null)

                    val id = storeService.createStore(storeName, addressLines, latitude, longitude)

                    emit(CreateStoreResultState.Success(id))
                } catch (e: Exception) {
                    log.error(e.toString())
                    emit(CreateStoreResultState.Error())
                }
            }
        }
    }

}