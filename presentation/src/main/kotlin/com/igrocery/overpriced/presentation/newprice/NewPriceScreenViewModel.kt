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

@HiltViewModel
class NewPriceScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
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

    fun updateCategoryId(categoryId: Long) {
        with (uiState) {
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
                    initialValue = LoadingState.NotLoading()
                )
        }
    }

    fun updateStoreId(storeId: Long) {
        with (uiState) {
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
                    initialValue = LoadingState.NotLoading()
                )
        }
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
