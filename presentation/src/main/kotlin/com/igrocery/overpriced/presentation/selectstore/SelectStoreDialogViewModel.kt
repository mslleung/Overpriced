package com.igrocery.overpriced.presentation.selectstore

import androidx.compose.runtime.Stable
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class SelectStoreDialogViewModel @Inject constructor(
    private val storeService: StoreService,
) : ViewModel() {

    @Stable
    data class ViewModelState(
        val isPreferredCurrencyLoaded: Boolean = false,
        val storesPagingDataFlow: Flow<PagingData<Store>> = emptyFlow(),
    )

    var uiState by mutableStateOf(ViewModelState())
        private set

    init {
        uiState = uiState.copy(
            storesPagingDataFlow = Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                storeService.getStoresPagingSource()
            }.flow
                .cachedIn(viewModelScope)
        )
    }

}
