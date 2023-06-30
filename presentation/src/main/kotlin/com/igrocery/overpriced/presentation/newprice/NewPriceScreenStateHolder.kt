package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantity
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryScreenResultViewModel
import com.igrocery.overpriced.presentation.selectstore.SelectStoreScreenResultViewModel

class NewPriceScreenStateHolder(
    private val newPriceScreenViewModel: NewPriceScreenViewModelState,
    isRequestingFirstFocus: Boolean,
    wantToShowSuggestionBox: Boolean,
    productName: String,
    productQuantity: String,
    productCategoryId: CategoryId?,
    priceAmountText: String,
    saleQuantity: SaleQuantity,
    priceIsSale: Boolean,
    priceStoreId: StoreId?,
    isDiscardDialogShown: Boolean,
    submitError: SubmitError
) {
    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)
    var wantToShowSuggestionBox by mutableStateOf(wantToShowSuggestionBox)

    var productName by mutableStateOf(productName)
    var productQuantity by mutableStateOf(productQuantity)
    var productCategoryId: CategoryId? = productCategoryId
        set(value) {
            field = value
            newPriceScreenViewModel.updateCategoryId(value)
        }
    var priceAmountText by mutableStateOf(priceAmountText)
    var saleQuantity by mutableStateOf(saleQuantity)
    var priceIsSale by mutableStateOf(priceIsSale)

    var priceStoreId: StoreId? = priceStoreId
        set(value) {
            field = value
            newPriceScreenViewModel.updateStoreId(value)
        }

    var isDiscardDialogShown by mutableStateOf(isDiscardDialogShown)

    init {
        // trigger setters
        this.productCategoryId = productCategoryId
        this.priceStoreId = priceStoreId
    }

    enum class SubmitError {
        None,
        ProductNameShouldNotBeEmpty,
        InvalidPriceAmount,
        StoreCannotBeEmpty
    }

    var submitError by mutableStateOf(submitError)

    fun hasModifications(): Boolean {
        return productName.isNotBlank()
                || productQuantity.isNotBlank()
                || productCategoryId != null
                || priceAmountText.isNotBlank()
                || priceStoreId != null
    }

    companion object {
        fun Saver(
            newPriceScreenViewModel: NewPriceScreenViewModelState,
            selectCategoryResultViewModel: SelectCategoryScreenResultViewModel,
            selectStoreResultViewModel: SelectStoreScreenResultViewModel
        ) = listSaver<NewPriceScreenStateHolder, Any?>(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.wantToShowSuggestionBox,
                    it.productName,
                    it.productQuantity,
                    it.productCategoryId,
                    it.priceAmountText,
                    it.saleQuantity,
                    it.priceIsSale,
                    it.priceStoreId,
                    it.isDiscardDialogShown,
                    it.submitError
                )
            },
            restore = {
                val productCategoryId =
                    selectCategoryResultViewModel.consumeResults()?.categoryId ?: it.getOrNull(4) as? CategoryId
                val productStoreId =
                    selectStoreResultViewModel.consumeResults()?.storeId ?: it.getOrNull(8) as? StoreId
                NewPriceScreenStateHolder(
                    newPriceScreenViewModel = newPriceScreenViewModel,
                    isRequestingFirstFocus = it[0] as Boolean,
                    wantToShowSuggestionBox = it[1] as Boolean,
                    productName = it[2] as String,
                    productQuantity = it[3] as String,
                    productCategoryId = productCategoryId,
                    priceAmountText = it[5] as String,
                    saleQuantity = it[6] as SaleQuantity,
                    priceIsSale = it[7] as Boolean,
                    priceStoreId = productStoreId,
                    isDiscardDialogShown = it[9] as Boolean,
                    submitError = it[10] as SubmitError,
                )
            }
        )
    }

}

@Composable
fun rememberNewPriceScreenState(
    args: NewPriceScreenArgs,
    newPriceScreenViewModel: NewPriceScreenViewModelState,
    selectCategoryResultViewModel: SelectCategoryScreenResultViewModel,
    selectStoreResultViewModel: SelectStoreScreenResultViewModel
) = rememberSaveable(
    stateSaver = Saver(
        save = {
            with(
                NewPriceScreenStateHolder.Saver(
                    newPriceScreenViewModel,
                    selectCategoryResultViewModel,
                    selectStoreResultViewModel
                )
            ) { save(it) }
        },
        restore = { value ->
            with(
                NewPriceScreenStateHolder.Saver(
                    newPriceScreenViewModel,
                    selectCategoryResultViewModel,
                    selectStoreResultViewModel
                )
            ) { restore(value)!! }
        }
    )
) {
    mutableStateOf(
        NewPriceScreenStateHolder(
            newPriceScreenViewModel = newPriceScreenViewModel,
            isRequestingFirstFocus = true,
            wantToShowSuggestionBox = false,
            productName = "",
            productQuantity = "",
            productCategoryId = args.categoryId,
            priceAmountText = "",
            saleQuantity = SaleQuantity.One,
            priceIsSale = false,
            priceStoreId = null,
            isDiscardDialogShown = false,
            submitError = NewPriceScreenStateHolder.SubmitError.None,
        )
    )
}
