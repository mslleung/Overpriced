package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class NewPriceScreenStateHolder {

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

    fun hasModifications(): Boolean {
        return productName.isNotBlank()
                || productDescription.isNotBlank()
                || productCategoryId != 0L
                || priceAmountText.isNotBlank()
                || priceStoreId != 0L
    }

}

@Composable
fun rememberNewPriceScreenState() = rememberSaveable(
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
            NewPriceScreenStateHolder().apply {
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
    mutableStateOf(NewPriceScreenStateHolder())
}
