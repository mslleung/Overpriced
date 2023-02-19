package com.igrocery.overpriced.presentation.newprice

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
import javax.inject.Inject

interface SelectStoreDialogViewModelState {
    var storesPagingDataFlow: Flow<PagingData<Store>>
}

@HiltViewModel
class SelectStoreDialogViewModel @Inject constructor(
    private val storeService: StoreService,
) : ViewModel(), SelectStoreDialogViewModelState {

    override var storesPagingDataFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        storeService.getStoresPagingSource()
    }.flow
        .cachedIn(viewModelScope)

}
