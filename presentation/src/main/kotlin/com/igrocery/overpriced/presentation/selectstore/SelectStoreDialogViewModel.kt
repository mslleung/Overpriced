package com.igrocery.overpriced.presentation.selectstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.productpricehistory.StoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectStoreDialogViewModel @Inject constructor(
    private val storeService: StoreService,
) : ViewModel() {

    val storesPagedFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        storeService.getStoresPagingSource()
    }.flow
        .cachedIn(viewModelScope)

}
