package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.*

class SelectCurrencyScreenStateHolder(savedState: List<*>? = null) {

    var isInitialScroll by mutableStateOf(savedState?.get(0) as? Boolean ?: true)
    val availableCurrencies: List<Currency> = Currency.getAvailableCurrencies()
        .sortedBy {
            it.currencyCode
        }

}

@Composable
fun rememberSelectCurrencyScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.isInitialScroll
            )
        },
        restore = { savedState ->
            SelectCurrencyScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(SelectCurrencyScreenStateHolder())
}
