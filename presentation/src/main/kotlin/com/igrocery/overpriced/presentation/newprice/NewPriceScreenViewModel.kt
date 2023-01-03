package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
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
    val categoryFlow: StateFlow<LoadingState<Category?>>
    val preferredCurrencyFlow: StateFlow<LoadingState<Currency>>
    val storesCountFlow: StateFlow<LoadingState<Int>>
    val storeFlow: StateFlow<LoadingState<Store?>>

    val submitResultState: LoadingState<Unit>

    fun updateCategoryId(categoryId: Long?)
    fun updateStoreId(storeId: Long?)
}

@HiltViewModel
class NewPriceScreenViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val productService: ProductService,
    private val priceRecordService: PriceRecordService,
    private val storeService: StoreService,
    preferenceService: PreferenceService
) : ViewModel(), NewPriceScreenViewModelState {

    override var categoryFlow: StateFlow<LoadingState<Category?>>
            by mutableStateOf(MutableStateFlow<LoadingState<Category?>>(LoadingState.NotLoading()))
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

    override var storeFlow: StateFlow<LoadingState<Store?>>
            by mutableStateOf(MutableStateFlow<LoadingState<Store?>>(LoadingState.NotLoading()))
        private set

    override var submitResultState: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())

    var query = ""
    val suggestedProductsPagingDataFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        productService.searchProductsByNamePaging("$query*")
    }.flow
        .cachedIn(viewModelScope)

    override fun updateCategoryId(categoryId: Long?) {
        categoryFlow = MutableStateFlow(LoadingState.Loading())
        categoryFlow = if (categoryId == null) {
            MutableStateFlow(LoadingState.Success(null))
        } else {
            categoryService.getCategoryById(categoryId)
                .map {
                    LoadingState.Success(it)
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadingState.Loading()
                )
        }
    }

    override fun updateStoreId(storeId: Long?) {
        storeFlow = if (storeId == null) {
            MutableStateFlow(LoadingState.Success(null))
        } else {
            storeService.getStoreById(storeId)
                .map {
                    LoadingState.Success(it)
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadingState.Loading()
                )
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
                    if (existingProduct.categoryId != productCategoryId) {
                        val updatedProduct = existingProduct.copy(
                            categoryId = productCategoryId
                        )
                        productService.updateProduct(updatedProduct)
                    }

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

    fun clearError() {
        submitResultState = LoadingState.NotLoading()
    }
}
