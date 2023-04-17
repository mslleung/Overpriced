package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantityUnit
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryScreenResultViewModel
import com.igrocery.overpriced.presentation.selectstore.SelectStoreScreenResultViewModel

class NewPriceScreenStateHolder(
    private val newPriceScreenViewModel: NewPriceScreenViewModelState,
    isRequestingFirstFocus: Boolean,
    wantToShowSuggestionBox: Boolean,
    productName: String,
    productQuantityAmountText: String,
    productQuantityUnit: ProductQuantityUnit,
    productCategoryId: CategoryId?,
    priceAmountText: String,
    saleQuantityAmountText: String,
    saleQuantityUnit: SaleQuantityUnit,
    priceIsSale: Boolean,
    priceStoreId: StoreId?,
    isDiscardDialogShown: Boolean,
    submitError: SubmitError
) {
    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)
    var wantToShowSuggestionBox by mutableStateOf(wantToShowSuggestionBox)

    var productName by mutableStateOf(productName)
    var productQuantityAmountText by mutableStateOf(productQuantityAmountText)
    var productQuantityUnit by mutableStateOf(productQuantityUnit)
    var productCategoryId: CategoryId? = productCategoryId
        set(value) {
            field = value
            newPriceScreenViewModel.updateCategoryId(value)
        }
    var priceAmountText by mutableStateOf(priceAmountText)
    var saleQuantityAmountText by mutableStateOf(saleQuantityAmountText)
    var saleQuantityUnit by mutableStateOf(saleQuantityUnit)
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
        InvalidProductQuantityAmount,
        InvalidPriceAmount,
        InvalidSaleQuantityAmount,
        StoreCannotBeEmpty
    }

    var submitError by mutableStateOf(submitError)

    fun hasModifications(): Boolean {
        return productName.isNotBlank()
                || productQuantityAmountText.isNotBlank()
                || productCategoryId != null
                || priceAmountText.isNotBlank()
                || saleQuantityAmountText.isNotBlank()
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
                    it.productQuantityAmountText,
                    it.productQuantityUnit,
                    it.productCategoryId,
                    it.priceAmountText,
                    it.saleQuantityAmountText,
                    it.saleQuantityUnit,
                    it.priceIsSale,
                    it.priceStoreId,
                    it.isDiscardDialogShown,
                    it.submitError
                )
            },
            restore = {
                val productCategoryId =
                    selectCategoryResultViewModel.consumeResults()?.categoryId ?: it.getOrNull(5) as? CategoryId
                val productStoreId =
                    selectStoreResultViewModel.consumeResults()?.storeId ?: it.getOrNull(10) as? StoreId
                NewPriceScreenStateHolder(
                    newPriceScreenViewModel = newPriceScreenViewModel,
                    isRequestingFirstFocus = it[0] as Boolean,
                    wantToShowSuggestionBox = it[1] as Boolean,
                    productName = it[2] as String,
                    productQuantityAmountText = it[3] as String,
                    productQuantityUnit = it[4] as ProductQuantityUnit,
                    productCategoryId = productCategoryId,
                    priceAmountText = it[6] as String,
                    saleQuantityAmountText = it[7] as String,
                    saleQuantityUnit = it[8] as SaleQuantityUnit,
                    priceIsSale = it[9] as Boolean,
                    priceStoreId = productStoreId,
                    isDiscardDialogShown = it[11] as Boolean,
                    submitError = it[12] as SubmitError,
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
            productQuantityAmountText = "",
            productQuantityUnit = ProductQuantityUnit.Pounds,
            productCategoryId = args.categoryId,
            priceAmountText = "",
            saleQuantityAmountText = "",
            saleQuantityUnit = SaleQuantityUnit.One,
            priceIsSale = false,
            priceStoreId = null,
            isDiscardDialogShown = false,
            submitError = NewPriceScreenStateHolder.SubmitError.None,
        )
    )
}
