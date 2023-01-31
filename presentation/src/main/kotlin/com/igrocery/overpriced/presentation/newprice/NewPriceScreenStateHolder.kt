package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.SavedStateHandle
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.presentation.editcategory.EditCategory_Result_CategoryId
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
    savedState: List<*>? = null
) {
    var isRequestingFirstFocus by mutableStateOf(savedState?.get(0) as? Boolean ?: true)
    var wantToShowSuggestionBox by mutableStateOf(savedState?.get(1) as? Boolean ?: false)

    var productName by mutableStateOf(savedState?.get(2) as? String ?: "")
    var productDescription by mutableStateOf(savedState?.get(3) as? String ?: "")
    var productCategoryId by mutableStateOf(savedState?.get(4) as? CategoryId?)
    var priceAmountText by mutableStateOf(savedState?.get(5) as? String ?: "")
    var priceStoreId by mutableStateOf(savedState?.get(6) as? StoreId?)

    var isDiscardDialogShown by mutableStateOf(savedState?.get(7) as? Boolean ?: false)
    var isSelectCategoryDialogShown by mutableStateOf(savedState?.get(8) as? Boolean ?: false)
    var isSelectStoreDialogShown by mutableStateOf(savedState?.get(9) as? Boolean ?: false)

    enum class SubmitError {
        None,
        ProductNameShouldNotBeEmpty,
        InvalidPriceAmount,
        StoreCannotBeEmpty
    }

    var submitError by mutableStateOf(savedState?.get(10) as? SubmitError ?: SubmitError.None)

    init {
        val productId = savedStateHandle.get<Long>(NewPrice_Arg_ProductId)?.let { ProductId(it) }
        val categoryId = savedStateHandle.get<Long>(NewPrice_Arg_CategoryId)?.let { CategoryId(it) }

        productCategoryId =
            savedStateHandle.get<Long>(NewCategory_Result_CategoryId)?.let { CategoryId(it) }
            ?: savedStateHandle.get<Long>(EditCategory_Result_CategoryId)?.let { CategoryId(it) }
            ?: productCategoryId

        priceStoreId = savedStateHandle.get<Long>(NewStore_Result_StoreId)?.let { StoreId(it) }
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

}

@Composable
fun rememberNewPriceScreenState(
    savedStateHandle: SavedStateHandle,
    coroutineScope: CoroutineScope,
    newPriceScreenViewModel: NewPriceScreenViewModelState
) = rememberSaveable(
    inputs = arrayOf(savedStateHandle, coroutineScope, newPriceScreenViewModel),
    stateSaver = listSaver(
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
        restore = { savedState ->
            NewPriceScreenStateHolder(
                savedStateHandle,
                coroutineScope,
                newPriceScreenViewModel,
                savedState
            )
        }
    )
) {
    mutableStateOf(
        NewPriceScreenStateHolder(
            savedStateHandle,
            coroutineScope,
            newPriceScreenViewModel
        )
    )
}
