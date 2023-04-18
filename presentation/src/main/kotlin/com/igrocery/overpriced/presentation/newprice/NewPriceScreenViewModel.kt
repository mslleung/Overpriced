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
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.StoreId
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
    val productFlow: StateFlow<LoadingState<Product>>
    val categoryFlow: StateFlow<LoadingState<Category?>>
    val preferredCurrencyFlow: StateFlow<LoadingState<Currency>>
    val storeFlow: StateFlow<LoadingState<Store?>>

    val submitResultState: LoadingState<Unit>

    fun updateCategoryId(categoryId: CategoryId?)
    fun updateStoreId(storeId: StoreId?)
}

@HiltViewModel
class NewPriceScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryService: CategoryService,
    private val productService: ProductService,
    private val priceRecordService: PriceRecordService,
    private val storeService: StoreService,
    preferenceService: PreferenceService
) : ViewModel(), NewPriceScreenViewModelState {

    private val args = NewPriceScreenArgs(savedStateHandle)

    override val productFlow: StateFlow<LoadingState<Product>> =
        args.productId?.let { productId ->
            productService.getProduct(productId)
                .map { LoadingState.Success(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadingState.Loading()
                )
        } ?: MutableStateFlow<LoadingState<Product>>(LoadingState.NotLoading()).asStateFlow()

    override var categoryFlow: StateFlow<LoadingState<Category?>> =
        MutableStateFlow<LoadingState<Category?>>(LoadingState.NotLoading())
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

    override var storeFlow: StateFlow<LoadingState<Store?>> =
        MutableStateFlow<LoadingState<Store?>>(LoadingState.NotLoading())
        private set

    override var submitResultState: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())

    var query = ""
    val suggestedProductsPagingDataFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        productService.searchProductsPaging("$query*")
    }.flow
        .cachedIn(viewModelScope)

    override fun updateCategoryId(categoryId: CategoryId?) {
        categoryFlow = MutableStateFlow(LoadingState.Loading())
        categoryFlow = if (categoryId == null) {
            MutableStateFlow(LoadingState.Success(null))
        } else {
            categoryService.getCategory(categoryId)
                .map { LoadingState.Success<Category?>(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadingState.Loading()
                )
        }
    }

    override fun updateStoreId(storeId: StoreId?) {
        storeFlow = if (storeId == null) {
            MutableStateFlow(LoadingState.Success(null))
        } else {
            storeService.getStore(storeId)
                .map { LoadingState.Success<Store?>(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadingState.Loading()
                )
        }
    }

    fun submitForm(
        productName: String,
        productQuantityAmountText: String,
        productQuantityUnit: ProductQuantityUnit,
        productCategoryId: CategoryId?,
        priceAmountText: String,
        saleQuantity: SaleQuantity,
        isSale: Boolean,
        priceStoreId: StoreId,
    ) {
        viewModelScope.launch {
            try {
                submitResultState = LoadingState.Loading()

                val existingProduct =
                    productService.getProduct(
                        productName,
                        ProductQuantity(productQuantityAmountText.toDouble(), productQuantityUnit)
                    ).first()

                if (existingProduct == null) {
                    productService.createProductWithPriceRecord(
                        productName,
                        productQuantityAmountText,
                        productQuantityUnit,
                        productCategoryId,
                        priceAmountText,
                        saleQuantity,
                        isSale,
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
                        existingProduct.id,
                        priceAmountText,
                        saleQuantity,
                        isSale,
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
