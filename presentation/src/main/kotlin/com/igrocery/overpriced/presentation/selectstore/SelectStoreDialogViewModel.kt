package com.igrocery.overpriced.presentation.selectstore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class SelectStoreDialogViewModel @Inject constructor(
    private val storeService: StoreService,
) : ViewModel() {

    class ViewModelState {
        var storesPagingDataFlow by mutableStateOf(emptyFlow<PagingData<Store>>())
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            storesPagingDataFlow = Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                storeService.getStoresPagingSource()
            }.flow
                .cachedIn(viewModelScope)
        }
    }

}
