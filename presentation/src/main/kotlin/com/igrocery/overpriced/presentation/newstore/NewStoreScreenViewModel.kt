package com.igrocery.overpriced.presentation.newstore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class NewStoreScreenViewModel @Inject constructor(
    private val storeService: StoreService
) : ViewModel() {

    class ViewModelState {
        var createStoreResultState: LoadingState<Long> by mutableStateOf(LoadingState.Loading())
    }

    val uiState = ViewModelState()

    fun createStore(
        storeName: String,
        addressLines: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            try {
                val id = storeService.createStore(storeName, addressLines, latitude, longitude)
                uiState.createStoreResultState = LoadingState.Success(id)
            } catch (e: Exception) {
                log.error(e.toString())
                uiState.createStoreResultState = LoadingState.Error(e)
            }
        }
    }

}