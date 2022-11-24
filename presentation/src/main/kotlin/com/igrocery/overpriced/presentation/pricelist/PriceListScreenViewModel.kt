package com.igrocery.overpriced.presentation.pricelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.NavDestinations
import com.igrocery.overpriced.presentation.productlist.ProductListScreenViewModelState
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface PriceListScreenViewModelState {
    val categoryFlow: StateFlow<LoadingState<Category?>>
    val currencyFlow: StateFlow<LoadingState<Currency>>
    val productsWithMinMaxPricesPagingDataFlow: Flow<PagingData<ProductWithMinMaxPrices>>
}

@HiltViewModel
class PriceListScreenViewModel @Inject constructor(
    savedState: SavedStateHandle,
    categoryService: CategoryService,
    private val productService: ProductService,
    preferenceService: PreferenceService,
) : ViewModel(), ProductListScreenViewModelState {

    private val categoryId = savedState.get<Long>(NavDestinations.ProductList_Arg_CategoryId).takeIf { it != 0L }

    override val categoryFlow = if (categoryId == null)
        MutableStateFlow<LoadingState<Category?>>(LoadingState.Success(null))
    else
        categoryService.getCategoryById(categoryId)
            .map {
                LoadingState.Success(it)
            }
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
    override val productsWithMinMaxPricesPagingDataFlow =
        preferenceService.getAppPreference().flatMapLatest {
            Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                productService.getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
                    categoryId,
                    it.preferredCurrency
                )
            }.flow
                .cachedIn(viewModelScope)
        }

}
