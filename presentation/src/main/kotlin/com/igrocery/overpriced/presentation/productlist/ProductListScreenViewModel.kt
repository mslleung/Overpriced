package com.igrocery.overpriced.presentation.productlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.presentation.categorybase.NavDestinations.ProductList_Arg_CategoryId
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface ProductListScreenViewModelState {
    val categoryFlow: StateFlow<LoadingState<Category?>>
    val productsPagingDataFlow: Flow<PagingData<Product>>
}

@HiltViewModel
class ProductListScreenViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val categoryService: CategoryService,
    private val productService: ProductService,
) : ViewModel(), ProductListScreenViewModelState {

    private val categoryId = savedState.get<Long>(ProductList_Arg_CategoryId) ?: 0L

    override val categoryFlow: StateFlow<LoadingState<Category?>>
        get() = categoryService.getCategoryById(categoryId)
            .map {
                LoadingState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = LoadingState.Loading()
            )

    override val productsPagingDataFlow: Flow<PagingData<Product>>
        get() = Pager(
            PagingConfig(
                pageSize = 100,
                prefetchDistance = 30
            )
        ) {
            productService.getProductsByCategoryIdPaging(categoryId)
        }.flow
            .cachedIn(viewModelScope)

}
