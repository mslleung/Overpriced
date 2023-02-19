package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.presentation.editcategory.EditCategory_Result_CategoryId
import com.igrocery.overpriced.presentation.editgrocerylist.GroceryListNameDialogStateHolder
import com.igrocery.overpriced.presentation.editstore.EditStore_Result_StoreId
import com.igrocery.overpriced.presentation.newcategory.NewCategory_Result_CategoryId
import com.igrocery.overpriced.presentation.newstore.NewStore_Result_StoreId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewPriceScreenStateHolder(
    savedStateHandle: SavedStateHandle,
    coroutineScope: CoroutineScope,
    newPriceScreenViewModel: NewPriceScreenViewModelState,
    isRequestingFirstFocus: Boolean,
    wantToShowSuggestionBox: Boolean,
    productName: String,
    productDescription: String,
    productCategoryId: CategoryId?,
    priceAmountText: String,
    priceStoreId: StoreId?,
    isDiscardDialogShown: Boolean,
    isSelectCategoryDialogShown: Boolean,
    isSelectStoreDialogShown: Boolean,
    submitError: SubmitError
) {
    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)
    var wantToShowSuggestionBox by mutableStateOf(wantToShowSuggestionBox)

    var productName by mutableStateOf(productName)
    var productDescription by mutableStateOf(productDescription)
    var productCategoryId by mutableStateOf(productCategoryId)
    var priceAmountText by mutableStateOf(priceAmountText)
    var priceStoreId by mutableStateOf(priceStoreId)

    var isDiscardDialogShown by mutableStateOf(isDiscardDialogShown)
    var isSelectCategoryDialogShown by mutableStateOf(isSelectCategoryDialogShown)
    var isSelectStoreDialogShown by mutableStateOf(isSelectStoreDialogShown)

    enum class SubmitError {
        None,
        ProductNameShouldNotBeEmpty,
        InvalidPriceAmount,
        StoreCannotBeEmpty
    }

    var submitError by mutableStateOf(submitError)

    init {
        val productId = savedStateHandle.get<Long>(NewPrice_Arg_ProductId)?.let { ProductId(it) }
        val categoryId = savedStateHandle.get<Long>(NewPrice_Arg_CategoryId)?.let { CategoryId(it) }

        this.productCategoryId =
            savedStateHandle.get<Long>(NewCategory_Result_CategoryId)?.let { CategoryId(it) }
                ?: savedStateHandle.get<Long>(EditCategory_Result_CategoryId)
                    ?.let { CategoryId(it) }
                        ?: productCategoryId

        this.priceStoreId = savedStateHandle.get<Long>(NewStore_Result_StoreId)?.let { StoreId(it) }
            ?: savedStateHandle.get<Long>(EditStore_Result_StoreId)?.let { StoreId(it) }
                    ?: priceStoreId

        coroutineScope.launch {
            snapshotFlow { productCategoryId }
                .collectLatest {
                    newPriceScreenViewModel.updateCategoryId(it)
                }
        }

        coroutineScope.launch {
            snapshotFlow { priceStoreId }
                .collectLatest {
                    newPriceScreenViewModel.updateStoreId(it)
                }
        }

        // consume all arguments
        savedStateHandle.remove<Long>(NewPrice_Arg_ProductId)
        savedStateHandle.remove<Long>(NewPrice_Arg_CategoryId)
        savedStateHandle.remove<Long>(NewCategory_Result_CategoryId)
        savedStateHandle.remove<Long>(EditCategory_Result_CategoryId)
        savedStateHandle.remove<Long>(NewStore_Result_StoreId)
        savedStateHandle.remove<Long>(EditStore_Result_StoreId)
    }

    fun hasModifications(): Boolean {
        return productName.isNotBlank()
                || productDescription.isNotBlank()
                || productCategoryId != null
                || priceAmountText.isNotBlank()
                || priceStoreId != null
    }

    companion object {
        fun Saver(
            savedStateHandle: SavedStateHandle,
            coroutineScope: CoroutineScope,
            newPriceScreenViewModel: NewPriceScreenViewModelState,
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
                    it.isSelectCategoryDialogShown,
                    it.isSelectStoreDialogShown,
                    it.submitError
                )
            },
            restore = {
                NewPriceScreenStateHolder(
                    savedStateHandle = savedStateHandle,
                    coroutineScope = coroutineScope,
                    newPriceScreenViewModel = newPriceScreenViewModel,
                    isRequestingFirstFocus = it[0] as Boolean,
                    wantToShowSuggestionBox = it[1] as Boolean,
                    productName = it[2] as String,
                    productDescription = it[3] as String,
                    productCategoryId = it[4] as CategoryId?,
                    priceAmountText = it[5] as String,
                    priceStoreId = it[6] as StoreId?,
                    isDiscardDialogShown = it[7] as Boolean,
                    isSelectCategoryDialogShown = it[8] as Boolean,
                    isSelectStoreDialogShown = it[9] as Boolean,
                    submitError = it[10] as SubmitError,
                )
            }
        )
    }

}

@Composable
fun rememberNewPriceScreenState(
    savedStateHandle: SavedStateHandle,
    coroutineScope: CoroutineScope,
    newPriceScreenViewModel: NewPriceScreenViewModelState
) = rememberSaveable(
    inputs = arrayOf(savedStateHandle, coroutineScope, newPriceScreenViewModel),
    stateSaver = Saver(
        save = {
            with(
                NewPriceScreenStateHolder.Saver(
                    savedStateHandle,
                    coroutineScope,
                    newPriceScreenViewModel
                )
            ) { save(it) }
        },
        restore = { value ->
            with(
                NewPriceScreenStateHolder.Saver(
                    savedStateHandle,
                    coroutineScope,
                    newPriceScreenViewModel
                )
            ) { restore(value)!! }
        }
    )
) {
    mutableStateOf(
        NewPriceScreenStateHolder(
            savedStateHandle = savedStateHandle,
            coroutineScope = coroutineScope,
            newPriceScreenViewModel = newPriceScreenViewModel,
            isRequestingFirstFocus = true,
            wantToShowSuggestionBox = false,
            productName = "",
            productDescription = "",
            productCategoryId = null,
            priceAmountText = "",
            priceStoreId = null,
            isDiscardDialogShown = false,
            isSelectCategoryDialogShown = false,
            isSelectStoreDialogShown = false,
            submitError = NewPriceScreenStateHolder.SubmitError.None,
        )
    )
}
