package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NewPriceScreenStateHolder(uiScope: CoroutineScope, viewModel: NewPriceScreenViewModel) {

    var isRequestingFirstFocus by mutableStateOf(true)
    var wantToShowSuggestionBox by mutableStateOf(false)

    var productName by mutableStateOf("")
    var productDescription by mutableStateOf("")
    var productCategoryId by mutableStateOf(0L)
    var priceAmountText by mutableStateOf("")
    var priceStoreId by mutableStateOf(0L)

    var isDiscardDialogShown by mutableStateOf(false)
    var isSelectCategoryDialogShown by mutableStateOf(false)
    var isSelectStoreDialogShown by mutableStateOf(false)

    init {
        snapshotFlow { productName }
            .onEach {
                viewModel.updateQuery(it)
            }
            .launchIn(uiScope)

        snapshotFlow { productCategoryId }
            .onEach {
                viewModel.updateCategoryId(it)
            }
            .launchIn(uiScope)

        snapshotFlow { priceStoreId }
            .onEach {
                viewModel.updateStoreId(it)
            }
            .launchIn(uiScope)
    }

    fun hasModifications(): Boolean {
        return productName.isNotBlank()
                || productDescription.isNotBlank()
                || productCategoryId != 0L
                || priceAmountText.isNotBlank()
                || priceStoreId != 0L
    }

}

@Composable
fun rememberNewPriceScreenState(uiScope: CoroutineScope, viewModel: NewPriceScreenViewModel) = rememberSaveable(
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
                it.isSelectStoreDialogShown
            )
        },
        restore = {
            NewPriceScreenStateHolder(uiScope, viewModel).apply {
                isRequestingFirstFocus = it[0] as Boolean
                wantToShowSuggestionBox = it[1] as Boolean
                productName = it[2] as String
                productDescription = it[3] as String
                productCategoryId = it[4] as Long
                priceAmountText = it[5] as String
                priceStoreId = it[6] as Long
                isDiscardDialogShown = it[7] as Boolean
                isSelectCategoryDialogShown = it[8] as Boolean
                isSelectStoreDialogShown = it[9] as Boolean
            }
        }
    )
) {
    mutableStateOf(NewPriceScreenStateHolder(uiScope, viewModel))
}
