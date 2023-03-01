package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.presentation.selectcategory.SelectCategoryScreenResultViewModel

class NewPriceScreenStateHolder(
    private val newPriceScreenViewModel: NewPriceScreenViewModelState,
    isRequestingFirstFocus: Boolean,
    wantToShowSuggestionBox: Boolean,
    productName: String,
    productDescription: String,
    productCategoryId: CategoryId?,
    priceAmountText: String,
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
                || productDescription.isNotBlank()
                || productCategoryId != null
                || priceAmountText.isNotBlank()
                || priceStoreId != null
    }

    companion object {
        fun Saver(
            newPriceScreenViewModel: NewPriceScreenViewModelState,
            selectCategoryResultViewModel: SelectCategoryScreenResultViewModel
        ) = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.wantToShowSuggestionBox,
                    it.productName,
                    it.productDescription,
                    it.productCategoryId,
                    it.priceAmountText,
                    it.priceStoreId,
                    it.isDiscardDialogShown,
                    it.submitError
                )
            },
            restore = {
                val productCategoryId =
                    selectCategoryResultViewModel.consumeResults()?.categoryId ?: it[4]
                NewPriceScreenStateHolder(
                    newPriceScreenViewModel = newPriceScreenViewModel,
                    isRequestingFirstFocus = it[0] as Boolean,
                    wantToShowSuggestionBox = it[1] as Boolean,
                    productName = it[2] as String,
                    productDescription = it[3] as String,
                    productCategoryId = productCategoryId,
                    priceAmountText = it[5] as String,
                    priceStoreId = it[6] as StoreId?,
                    isDiscardDialogShown = it[7] as Boolean,
                    submitError = it[8] as SubmitError,
                )
            }
        )
    }

}

@Composable
fun rememberNewPriceScreenState(
    args: NewPriceScreenArgs,
    newPriceScreenViewModel: NewPriceScreenViewModelState,
    selectCategoryResultViewModel: SelectCategoryScreenResultViewModel
) = rememberSaveable(
    inputs = arrayOf(args, newPriceScreenViewModel, selectCategoryResultViewModel),
    stateSaver = Saver(
        save = {
            with(
                NewPriceScreenStateHolder.Saver(
                    newPriceScreenViewModel,
                    selectCategoryResultViewModel
                )
            ) { save(it) }
        },
        restore = { value ->
            with(
                NewPriceScreenStateHolder.Saver(
                    newPriceScreenViewModel,
                    selectCategoryResultViewModel
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
            priceStoreId = null,
            isDiscardDialogShown = false,
            submitError = NewPriceScreenStateHolder.SubmitError.None,
        )
    )
}
