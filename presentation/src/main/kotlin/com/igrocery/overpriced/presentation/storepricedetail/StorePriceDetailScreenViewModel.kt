package com.igrocery.overpriced.presentation.storepricedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.application.productpricehistory.PriceRecordService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface StorePriceDetailScreenViewModelState {
    val productFlow: StateFlow<LoadingState<Product>>
    val storeFlow: StateFlow<LoadingState<Store>>
    val currencyFlow: StateFlow<LoadingState<Currency>>
    val priceRecordsPagingDataFlow: Flow<PagingData<PriceRecord>>
}

@HiltViewModel
class StorePriceDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    preferenceService: PreferenceService,
    productService: ProductService,
    storeService: StoreService,
    priceRecordService: PriceRecordService
) : ViewModel(), StorePriceDetailScreenViewModelState {

    private val args = StorePriceDetailScreenArgs(savedStateHandle)

    override val productFlow = productService.getProduct(args.productId)
        .map { LoadingState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    override val storeFlow = storeService.getStore(args.storeId)
        .map { LoadingState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    override val currencyFlow = preferenceService.getAppPreference()
        .map {
            LoadingState.Success(it.preferredCurrency)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val priceRecordsPagingDataFlow =
        preferenceService.getAppPreference().flatMapLatest {
            Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                priceRecordService.getPriceRecordsPaging(
                    args.productId,
                    args.storeId,
                    it.preferredCurrency
                )
            }.flow
                .cachedIn(viewModelScope)
        }

}
