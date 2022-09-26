package com.igrocery.overpriced.presentation.newprice

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
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.application.productpricehistory.PriceRecordService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.application.productpricehistory.StoreService
import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.presentation.newprice.NewPriceScreenViewModel.SubmitFormResultState.ErrorReason
import com.igrocery.overpriced.presentation.newstore.NewStoreScreenViewModel
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreenViewModel
import com.igrocery.overpriced.presentation.shared.LoadingState
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

    private companion object {
        private const val KEY_QUERY = "KEY_QUERY"
    }

    private var query: String = savedState[KEY_QUERY] ?: ""

    class ViewModelState {
        var suggestedProductsPagingDataFlow by mutableStateOf(emptyFlow<PagingData<Product>>())
        var categoryFlow: StateFlow<LoadingState<Category>> by mutableStateOf(MutableStateFlow(LoadingState.NotLoading()))
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
        }
    }

    fun updateQuery(query: String) {
        this.query = query
    }

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
