package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.igrocery.overpriced.presentation.shared.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewPriceScreenStateHolder(
    coroutineScope: CoroutineScope,
    newPriceScreenViewModel: NewPriceScreenViewModelState,
    savedState: List<*>? = null
) {
    var isRequestingFirstFocus by mutableStateOf(savedState?.get(0) as? Boolean ?: true)
    var wantToShowSuggestionBox by mutableStateOf(savedState?.get(1) as? Boolean ?: false)

    var productName by mutableStateOf(savedState?.get(2) as? String ?: "")
    var productDescription by mutableStateOf(savedState?.get(3) as? String ?: "")
    var productCategoryId by mutableStateOf(savedState?.get(4) as? Long?)
    var priceAmountText by mutableStateOf(savedState?.get(5) as? String ?: "")
    var priceStoreId by mutableStateOf(savedState?.get(6) as? Long?)

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
        coroutineScope.launch {
            when (val categoryLoadingState = newPriceScreenViewModel.categoryFlow.value) {
                is LoadingState.Loading -> {
                    val categoryResult = newPriceScreenViewModel.categoryFlow
                        .filter { it is LoadingState.Success || it is LoadingState.Error }.first()
                    if (categoryResult is LoadingState.Success) {
                        productCategoryId = categoryResult.data?.id
                    }
                }
                is LoadingState.Success -> {
                    productCategoryId = categoryLoadingState.data?.id
                }
                else -> {}
            }

            snapshotFlow { productCategoryId }
                .collectLatest {
                    newPriceScreenViewModel.updateCategoryId(it)
                }
        }

        coroutineScope.launch {
            when (val storeLoadingState = newPriceScreenViewModel.storeFlow.value) {
                is LoadingState.Loading -> {
                    val storeResult = newPriceScreenViewModel.storeFlow
                        .filter { it is LoadingState.Success || it is LoadingState.Error }.first()
                    if (storeResult is LoadingState.Success) {
                        priceStoreId = storeResult.data?.id
                    }
                }
                is LoadingState.Success -> {
                    priceStoreId = storeLoadingState.data?.id
                }
                else -> {}
            }

            snapshotFlow { priceStoreId }
                .collectLatest {
                    newPriceScreenViewModel.updateStoreId(it)
                }
        }

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
    coroutineScope: CoroutineScope,
    newPriceScreenViewModel: NewPriceScreenViewModelState
) = rememberSaveable(
    inputs = arrayOf(coroutineScope, newPriceScreenViewModel),
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
            NewPriceScreenStateHolder(coroutineScope, newPriceScreenViewModel, savedState)
        }
    )
) {
    mutableStateOf(NewPriceScreenStateHolder(coroutineScope, newPriceScreenViewModel))
}
