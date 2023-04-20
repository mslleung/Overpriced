package com.igrocery.overpriced.presentation.selectstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SelectStoreScreenViewModelState {
    var storesPagingDataFlow: Flow<PagingData<Store>>
}

@HiltViewModel
class SelectStoreScreenViewModel @Inject constructor(
    private val storeService: StoreService,
) : ViewModel(), SelectStoreScreenViewModelState {

    override var storesPagingDataFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        storeService.getStoresPagingSource()
    }.flow
        .cachedIn(viewModelScope)

    fun deleteStore(store: Store) {
        viewModelScope.launch {
            storeService.deleteStore(store)
        }
    }
}
