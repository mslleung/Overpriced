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
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenViewModel.SubmitFormResultState.ErrorReason
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewPriceScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val categoryService: CategoryService,
    private val productService: ProductService,
    private val priceRecordService: PriceRecordService,
    private val storeService: StoreService,
    private val preferenceService: PreferenceService
) : ViewModel() {

    companion object {
        private const val KEY_PRODUCT_NAME = "KEY_PRODUCT_NAME"
        private const val KEY_PRODUCT_DESCRIPTION = "KEY_PRODUCT_DESCRIPTION"
        private const val KEY_PRODUCT_CATEGORY_ID = "KEY_PRODUCT_CATEGORY_ID"
        private const val KEY_BARCODE = "KEY_BARCODE"
        private const val KEY_STORE_ID = "KEY_STORE_ID"
    }

    val productNameFlow = savedState.getStateFlow(KEY_PRODUCT_NAME, "")

    val productDescriptionFlow = savedState.getStateFlow(KEY_PRODUCT_DESCRIPTION, "")

    val productsPagedFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        productService.searchProductsByNamePaging("${productNameFlow.value}*")
    }.flow
        .cachedIn(viewModelScope)

    val attachedBarcodeFlow = savedState.getStateFlow(KEY_BARCODE, null as String?)

    val productCategoryFlow = savedState.getStateFlow<Long?>(KEY_PRODUCT_CATEGORY_ID, null)
        .flatMapLatest {
            if (it == null) {
                flowOf(null)
            } else {
                categoryService.getCategoryById(it)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val preferredCurrencyFlow = preferenceService.getAppPreference()
        .map { it.preferredCurrency }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Currency.getInstance(Locale.getDefault())
        )

    val storesCountFlow = storeService.getStoreCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    val selectedStoreFlow = savedState.getStateFlow(KEY_STORE_ID, 0L)
        .flatMapLatest {
            storeService.getStoreById(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun setProductName(productName: String) {
        savedState[KEY_PRODUCT_NAME] = productName
    }

    fun setProductDescription(productDescription: String?) {
        savedState[KEY_PRODUCT_DESCRIPTION] = productDescription
    }

    fun setProductCategoryId(categoryId: Long?) {
        savedState[KEY_PRODUCT_CATEGORY_ID] = categoryId
    }

    fun setBarcode(barcode: String?) {
        savedState[KEY_BARCODE] = barcode

        if (barcode != null) {
            viewModelScope.launch {
                val product = productService.getProduct(barcode).first()
                if (product != null) {
                    setProductName(product.name)
                    setProductDescription(product.description)
                }
            }
        }
    }

    fun selectStore(storeId: Long) {
        savedState[KEY_STORE_ID] = storeId
    }

    fun hasModifications(): Boolean {
        return productNameFlow.value.isNotBlank()
                || productDescriptionFlow.value.isNotBlank()
                || attachedBarcodeFlow.value != null
                || productCategoryFlow.value != null
                || selectedStoreFlow.value != null
    }

    sealed interface SubmitFormResultState {
        object Success : SubmitFormResultState

        enum class ErrorReason {
            NameEmptyError,
            StoreNotSelectedError,
            PriceAmountInputError,
            PriceAmountInvalidError,
            UnknownError
        }

        data class Error(val reason: ErrorReason) : SubmitFormResultState
    }

    var submitFormResult by mutableStateOf<SubmitFormResultState?>(null)

    fun submitForm(
        productName: String,
        productDescription: String,
        productBarcode: String?,
        productCategoryId: Long?,
        priceAmountText: String,
        store: Store?,
    ) {
        viewModelScope.launch {
            try {
                val existingProduct =
                    productService.getProduct(productName, productDescription).first()

                if (existingProduct == null) {
                    productService.createProductWithPriceRecord(
                        productName,
                        productDescription,
                        productCategoryId,
                        productBarcode,
                        priceAmountText,
                        store?.id ?: 0L,
                    )
                } else {
                    // update product because category may be changed
                    val updatedProduct = Product(existingProduct)
                    updatedProduct.updateTimestamp = System.currentTimeMillis()
                    updatedProduct.categoryId = productCategoryId
                    productService.updateProduct(updatedProduct)

                    priceRecordService.createPriceRecord(
                        priceAmountText,
                        existingProduct.id,
                        store?.id ?: 0L,
                    )
                }

                submitFormResult = SubmitFormResultState.Success
            } catch (e: Product.BlankNameException) {
                log.error(e.toString())
                submitFormResult = SubmitFormResultState.Error(ErrorReason.NameEmptyError)
            } catch (e: Money.InvalidAmountException) {
                log.error(e.toString())
                submitFormResult = SubmitFormResultState.Error(ErrorReason.PriceAmountInvalidError)
            } catch (e: NumberFormatException) {
                log.error(e.toString())
                submitFormResult = SubmitFormResultState.Error(ErrorReason.PriceAmountInputError)
            } catch (e: PriceRecord.InvalidStoreIdException) {
                log.error(e.toString())
                submitFormResult = SubmitFormResultState.Error(ErrorReason.StoreNotSelectedError)
            } catch (e: Exception) {
                log.error(e.toString())
                submitFormResult = SubmitFormResultState.Error(ErrorReason.UnknownError)
            }
        }
    }
}
