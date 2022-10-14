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

interface NewStoreScreenViewModelState {
    val createStoreResultState: LoadingState<Long>
}

@HiltViewModel
class NewStoreScreenViewModel @Inject constructor(
    private val storeService: StoreService
) : ViewModel(), NewStoreScreenViewModelState {

    override var createStoreResultState: LoadingState<Long> by mutableStateOf(LoadingState.NotLoading())

    fun createStore(
        storeName: String,
        addressLines: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            try {
                createStoreResultState = LoadingState.Loading()

                val id = storeService.createStore(storeName, addressLines, latitude, longitude)
                createStoreResultState = LoadingState.Success(id)
            } catch (e: Exception) {
                log.error(e.toString())
                createStoreResultState = LoadingState.Error(e)
            }
        }
    }

}
