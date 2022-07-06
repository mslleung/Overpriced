package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class NewPriceScreenStateHolder {

    var isRequestingFirstFocus by mutableStateOf(true)
    var wantToShowSuggestionBox by mutableStateOf(false)

    var priceAmountText by mutableStateOf("")

    var isDiscardDialogShown by mutableStateOf(false)

    var isSelectStoreDialogShown by mutableStateOf(false)

    fun hasModifications(): Boolean {
        return priceAmountText.isNotBlank()
    }

    companion object {
        val Saver: Saver<NewPriceScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.wantToShowSuggestionBox,
                    it.priceAmountText,
                    it.isDiscardDialogShown,
                    it.isSelectStoreDialogShown
                )
            },
            restore = {
                NewPriceScreenStateHolder().apply {
                    isRequestingFirstFocus = it[0] as Boolean
                    wantToShowSuggestionBox = it[1] as Boolean
                    priceAmountText = it[2] as String
                    isDiscardDialogShown = it[3] as Boolean
                    isSelectStoreDialogShown = it[4] as Boolean
                }
            }
        )
    }
}

@Composable
fun rememberNewPriceScreenState() = rememberSaveable(
    stateSaver = NewPriceScreenStateHolder.Saver
) {
    mutableStateOf(NewPriceScreenStateHolder())
}
