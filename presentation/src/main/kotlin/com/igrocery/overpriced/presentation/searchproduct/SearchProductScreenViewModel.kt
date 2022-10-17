package com.igrocery.overpriced.presentation.searchproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface SearchProductScreenViewModelState

@HiltViewModel
class SearchProductScreenViewModel @Inject constructor(
    private val productService: ProductService,
) : ViewModel(), SearchProductScreenViewModelState {

    var query = ""

    val productsPagingDataFlow = Pager(
            PagingConfig(
                pageSize = 100,
                prefetchDistance = 30
            )
        ) {
            productService.searchProductsByNamePaging("$query*")
        }.flow
            .cachedIn(viewModelScope)

}
