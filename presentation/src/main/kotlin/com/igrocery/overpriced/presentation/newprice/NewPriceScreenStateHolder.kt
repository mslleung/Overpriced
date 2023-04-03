package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.PriceQuantityUnit
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryScreenResultViewModel
import com.igrocery.overpriced.presentation.selectstore.SelectStoreScreenResultViewModel

class NewPriceScreenStateHolder(
    private val newPriceScreenViewModel: NewPriceScreenViewModelState,
    isRequestingFirstFocus: Boolean,
    wantToShowSuggestionBox: Boolean,
    productName: String,
    productDescription: String,
    productCategoryId: CategoryId?,
    priceAmountText: String,
    quantityText: String,
    quantityUnit: PriceQuantityUnit,
    priceIsSale: Boolean,
    priceStoreId: StoreId?,
    isDiscardDialogShown: Boolean,
    submitError: SubmitError
) {
    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)
    var wantToShowSuggestionBox by mutableStateOf(wantToShowSuggestionBox)

    var productName by mutableStateOf(productName)
    var productDescription by mutableStateOf(productDescription)
    var productCategoryId: CategoryId? = productCategoryId
        set(value) {
            field = value
            newPriceScreenViewModel.updateCategoryId(value)
        }
    var priceAmountText by mutableStateOf(priceAmountText)
    var quantityText by mutableStateOf(quantityText)
    var quantityUnit by mutableStateOf(quantityUnit)
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
        InvalidQuantityAmount,
        StoreCannotBeEmpty
    }

    var submitError by mutableStateOf(submitError)

    fun hasModifications(): Boolean {
        return productName.isNotBlank()
                || productDescription.isNotBlank()
                || productCategoryId != null
                || priceAmountText.isNotBlank()
                || quantityText.isNotBlank()
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
                    it.productDescription,
                    it.productCategoryId,
                    it.priceAmountText,
                    it.quantityText,
                    it.quantityUnit,
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
                    selectStoreResultViewModel.consumeResults()?.storeId ?: it.getOrNull(9) as? StoreId
                NewPriceScreenStateHolder(
                    newPriceScreenViewModel = newPriceScreenViewModel,
                    isRequestingFirstFocus = it[0] as Boolean,
                    wantToShowSuggestionBox = it[1] as Boolean,
                    productName = it[2] as String,
                    productDescription = it[3] as String,
                    productCategoryId = productCategoryId,
                    priceAmountText = it[5] as String,
                    quantityText = it[6] as String,
                    quantityUnit = it[7] as PriceQuantityUnit,
                    priceIsSale = it[8] as Boolean,
                    priceStoreId = productStoreId,
                    isDiscardDialogShown = it[10] as Boolean,
                    submitError = it[11] as SubmitError,
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
            productDescription = "",
            productCategoryId = args.categoryId,
            priceAmountText = "",
            quantityText = "",
            quantityUnit = PriceQuantityUnit.Pieces,
            priceIsSale = false,
            priceStoreId = null,
            isDiscardDialogShown = false,
            submitError = NewPriceScreenStateHolder.SubmitError.None,
        )
    )
}
