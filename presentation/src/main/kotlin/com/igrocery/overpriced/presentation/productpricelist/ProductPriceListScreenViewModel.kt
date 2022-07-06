package com.igrocery.overpriced.presentation.productpricelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.application.productpricehistory.ProductService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class ProductPriceListScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val productService: ProductService
) : ViewModel() {

    val productsPagedFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        productService.getProductsPagingSource()
    }.flow
        .cachedIn(viewModelScope)

}
