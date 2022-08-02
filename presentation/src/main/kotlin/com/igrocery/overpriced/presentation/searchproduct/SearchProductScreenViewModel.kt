package com.igrocery.overpriced.presentation.searchproduct

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class SearchProductScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val productService: ProductService,
) : ViewModel() {

    companion object {
        private const val KEY_QUERY = "KEY_QUERY"
    }

    fun setQuery(query: String) {
        savedState[KEY_QUERY] = query
    }

    val productsPagedFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        val queryStr = savedState.get<String>(KEY_QUERY) ?: ""
        productService.searchProductsByNamePaging("$queryStr*")
    }.flow
        .cachedIn(viewModelScope)

}
