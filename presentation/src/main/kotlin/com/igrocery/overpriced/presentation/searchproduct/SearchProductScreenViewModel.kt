package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface SearchProductScreenViewModelState {
    val currencyFlow: StateFlow<LoadingState<Currency>>
    val productsWithMinMaxPricesPagingDataFlow: Flow<PagingData<ProductWithMinMaxPrices>>
}

private const val KEY_QUERY = "KEY_QUERY"

@HiltViewModel
class SearchProductScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productService: ProductService,
    preferenceService: PreferenceService,
) : ViewModel(), SearchProductScreenViewModelState {

    private val args = SearchProductScreenArgs(savedStateHandle)

    override val currencyFlow = preferenceService.getAppPreference()
        .map {
            LoadingState.Success(it.preferredCurrency)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    val queryFlow = savedStateHandle.getStateFlow(KEY_QUERY, args.query)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val productsWithMinMaxPricesPagingDataFlow =
        preferenceService.getAppPreference().flatMapLatest {
            Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                productService.searchProductsWithMinMaxPricesPaging(
                    "${queryFlow.value}*",
                    it.preferredCurrency
                )
            }.flow
                .cachedIn(viewModelScope)
        }

}
