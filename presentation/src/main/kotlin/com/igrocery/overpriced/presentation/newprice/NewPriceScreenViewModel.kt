package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.application.productpricehistory.PriceRecordService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class NewPriceScreenViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val productService: ProductService,
    private val priceRecordService: PriceRecordService,
    private val storeService: StoreService,
    private val preferenceService: PreferenceService
) : ViewModel() {

    private var query = ""

    class ViewModelState {
        var suggestedProductsPagingDataFlow by mutableStateOf(emptyFlow<PagingData<Product>>())
        var category: LoadingState<Category> by mutableStateOf(LoadingState.Loading())
        var preferredCurrency: LoadingState<Currency> by mutableStateOf(LoadingState.Loading())
        var storesCount: LoadingState<Int> by mutableStateOf(LoadingState.Loading())
        var store: LoadingState<Store> by mutableStateOf(LoadingState.Loading())

        var submitResultState: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            suggestedProductsPagingDataFlow = Pager(
                PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 30
                )
            ) {
                productService.searchProductsByNamePaging("$query*")
            }.flow
                .cachedIn(viewModelScope)

            preferenceService.getAppPreference()
                .onEach {
                    preferredCurrency = LoadingState.Success(it.preferredCurrency)
                }
                .launchIn(viewModelScope)

            storeService.getStoreCount()
                .onEach {
                    storesCount = LoadingState.Success(it)
                }
                .launchIn(viewModelScope)
        }
    }

    fun updateQuery(query: String) {
        this.query = query
    }

    suspend fun updateCategoryId(categoryId: Long) {
        with(uiState) {
            categoryService.getCategoryById(categoryId)
                .collectLatest {
                    category = if (it != null) {
                        LoadingState.Success(it)
                    } else {
                        LoadingState.Error(Exception("Category not found."))
                    }
                }
        }
    }

    suspend fun updateStoreId(storeId: Long) {
        with(uiState) {
            storeService.getStoreById(storeId)
                .collectLatest {
                    store = if (it != null) {
                        LoadingState.Success(it)
                    } else {
                        LoadingState.Error(Exception("Store not found."))
                    }
                }
        }
    }

    fun submitForm(
        productName: String,
        productDescription: String,
        productCategoryId: Long?,
        priceAmountText: String,
        priceStoreId: Long,
    ) {
        viewModelScope.launch {
            try {
                uiState.submitResultState = LoadingState.Loading()

                val existingProduct =
                    productService.getProduct(productName, productDescription).first()

                if (existingProduct == null) {
                    productService.createProductWithPriceRecord(
                        productName,
                        productDescription,
                        productCategoryId,
                        priceAmountText,
                        priceStoreId,
                    )
                } else {
                    // update product because category may be changed
                    val updatedProduct = existingProduct.copy(
                        categoryId = productCategoryId
                    )
                    productService.updateProduct(updatedProduct)

                    priceRecordService.createPriceRecord(
                        priceAmountText,
                        existingProduct.id,
                        priceStoreId,
                    )
                }

                uiState.submitResultState = LoadingState.Success(Unit)
            } catch (e: Exception) {
                log.error(e.toString())
                uiState.submitResultState = LoadingState.Error(e)
            }
        }
    }
}
