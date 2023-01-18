package com.igrocery.overpriced.presentation.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.presentation.NavDestinations
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface ProductDetailScreenViewModelState {
    val currencyFlow: StateFlow<LoadingState<Currency>>
    val productWithPricesFlow: StateFlow<LoadingState<ProductWithMinMaxPrices>>
    val storesWithMinMaxPricesPagingDataFlow: Flow<PagingData<StoreWithMinMaxPrices>>
}

@HiltViewModel
class ProductDetailScreenViewModel @Inject constructor(
    savedState: SavedStateHandle,
    preferenceService: PreferenceService,
    productService: ProductService,
    storeService: StoreService,
) : ViewModel(), ProductDetailScreenViewModelState {

    private val productId = savedState.get<Long>(NavDestinations.ProductDetail_Arg_ProductId)
        ?: throw IllegalArgumentException("Product id cannot be null")

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
    override val productWithPricesFlow =
        preferenceService.getAppPreference().flatMapLatest {
            productService.getProductWithMinMaxPrices(
                productId,
                it.preferredCurrency
            )
        }.map {
            if (it != null) {
                LoadingState.Success(it)
            } else {
                LoadingState.Error(IllegalArgumentException("Product not found"))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val storesWithMinMaxPricesPagingDataFlow =
        preferenceService.getAppPreference().flatMapLatest {
            Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                storeService.getStoresWithMinMaxPricesPaging(
                    productId,
                    it.preferredCurrency
                )
            }.flow
                .cachedIn(viewModelScope)
        }

}
