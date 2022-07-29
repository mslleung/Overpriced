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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchProductScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val categoryService: CategoryService,
    private val productService: ProductService,
) : ViewModel() {

    companion object {
        private const val KEY_QUERY = "KEY_QUERY"
    }

    val queryFlow = savedState.getStateFlow(KEY_QUERY, "")

    fun setQuery(query: String) {
        savedState[KEY_QUERY] = query
    }

    val productsPagedFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        productService.getProductsPagingSource(queryFlow.value)
    }.flow
        .cachedIn(viewModelScope)

    data class CategoryWithProduct(
        val category: Category,
        val productCount: Int,
    )

    val productCountWithNoCategory = productService.getProductCountWithCategory(null)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    val categoryListWithCountFlow = categoryService.getAllCategories()
        .flatMapLatest { categoryList ->
            combine(categoryList.map { productService.getProductCountWithCategory(it) }) {
                val productCountList = it.asList()
                categoryList.zip(productCountList) { category, count ->
                    CategoryWithProductCount(category, count)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
