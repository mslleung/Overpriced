package com.igrocery.overpriced.presentation.productlist

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

@HiltViewModel
class ProductListScreenViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val categoryService: CategoryService,
    private val productService: ProductService,
) : ViewModel() {

    private val categoryId = savedState.get<Long>(ProductList_Arg_CategoryId) ?: 0L

    class ViewModelState {
        var category: LoadingState<Category?> by mutableStateOf(LoadingState.Loading())
        var productsPagingDataFlow by mutableStateOf(emptyFlow<PagingData<Product>>())
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            categoryService.getCategoryById(categoryId)
                .onEach {
                    category = LoadingState.Success(it)
                }
                .launchIn(viewModelScope)

            productsPagingDataFlow = Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                productService.getProductsByCategoryIdPaging(categoryId)
            }.flow
                .cachedIn(viewModelScope)
        }
    }

}
