package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class SearchProductScreenViewModel @Inject constructor(
    private val productService: ProductService,
) : ViewModel() {

    class ViewModelState {
        var productsPagingDataFlow by mutableStateOf(emptyFlow<PagingData<Product>>())
    }

    val uiState = ViewModelState()

    private var query = ""

    init {
        uiState.productsPagingDataFlow = Pager(
            PagingConfig(
                pageSize = 100,
                prefetchDistance = 30
            )
        ) {
            productService.searchProductsByNamePaging("$query*")
        }.flow
            .cachedIn(viewModelScope)
        updateQuery("")
    }

    fun updateQuery(query: String) {
        this.query = query
    }

}
