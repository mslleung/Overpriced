package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.presentation.selectstore.SelectStoreDialogViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class SearchProductScreenViewModel @Inject constructor(
    private val productService: ProductService,
) : ViewModel() {

    @Stable
    data class ViewModelState(
        val productsPagingDataFlow: Flow<PagingData<Product>> = emptyFlow()
    )

    var uiState by mutableStateOf(ViewModelState())
        private set

    init {
        updateQuery("")
    }

    fun updateQuery(query: String) {
        uiState = uiState.copy(
            productsPagingDataFlow = Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                productService.searchProductsByNamePaging("$query*")
            }.flow
                .cachedIn(viewModelScope)
        )
    }

}
