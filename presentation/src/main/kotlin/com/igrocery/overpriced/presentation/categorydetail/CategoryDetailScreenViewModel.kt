package com.igrocery.overpriced.presentation.categorydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryDetailScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val categoryService: CategoryService,
    private val productService: ProductService,
) : ViewModel() {

    private companion object {
        private const val KEY_CATEGORY_ID = "KEY_CATEGORY_ID"
    }

    fun setCategoryId(categoryId: Long) {
        savedState[KEY_CATEGORY_ID] = categoryId
    }

    val categoryFlow = savedState.getStateFlow(KEY_CATEGORY_ID, -1L)
        .flatMapLatest { categoryService.getCategoryById(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val productsPagedFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        val categoryId = savedState.get<Long>(KEY_CATEGORY_ID) ?: -1L
        productService.getProductsByCategoryIdPaging(categoryId)
    }.flow
        .cachedIn(viewModelScope)

}
