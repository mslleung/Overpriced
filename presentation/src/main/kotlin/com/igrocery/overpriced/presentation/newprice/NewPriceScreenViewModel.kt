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

interface NewPriceScreenViewModelState {
    val categoryFlow: StateFlow<LoadingState<Category>>
    val preferredCurrencyFlow: StateFlow<LoadingState<Currency>>
    val storesCountFlow: StateFlow<LoadingState<Int>>
    val storeFlow: StateFlow<LoadingState<Store>>

    val submitResultState: LoadingState<Unit>
}

@HiltViewModel
class NewPriceScreenViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val productService: ProductService,
    private val priceRecordService: PriceRecordService,
    private val storeService: StoreService,
    private val preferenceService: PreferenceService
) : ViewModel(), NewPriceScreenViewModelState {

    private var query = ""

    override var categoryFlow: StateFlow<LoadingState<Category>>
            by mutableStateOf(MutableStateFlow<LoadingState<Category>>(LoadingState.Loading()))
        private set

    override val preferredCurrencyFlow = preferenceService.getAppPreference()
        .map {
            LoadingState.Success(it.preferredCurrency)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    override val storesCountFlow = storeService.getStoreCount()
        .map {
            LoadingState.Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    override var storeFlow: StateFlow<LoadingState<Store>>
            by mutableStateOf(MutableStateFlow<LoadingState<Store>>(LoadingState.Loading()))
        private set

    override var submitResultState: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())

    val suggestedProductsPagingDataFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        productService.searchProductsByNamePaging("$query*")
    }.flow
        .cachedIn(viewModelScope)

    fun updateQuery(query: String) {
        this.query = query
    }

    fun updateCategoryId(categoryId: Long) {
        categoryFlow = categoryService.getCategoryById(categoryId)
            .map {
                if (it != null) {
                    LoadingState.Success(it)
                } else {
                    LoadingState.Error(Exception("Category not found."))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = LoadingState.Loading()
            )
    }

    fun updateStoreId(storeId: Long) {
        storeFlow = storeService.getStoreById(storeId)
            .map {
                if (it != null) {
                    LoadingState.Success(it)
                } else {
                    LoadingState.Error(Exception("Store not found."))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = LoadingState.Loading()
            )
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
                submitResultState = LoadingState.Loading()

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

                submitResultState = LoadingState.Success(Unit)
            } catch (e: Exception) {
                log.error(e.toString())
                submitResultState = LoadingState.Error(e)
            }
        }
    }
}
