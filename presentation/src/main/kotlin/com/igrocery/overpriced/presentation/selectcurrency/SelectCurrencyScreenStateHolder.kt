package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.*

class SelectCurrencyScreenStateHolder {
    var isInitialScroll by mutableStateOf(true)
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
        restore = {
            SelectCurrencyScreenStateHolder().apply {
                isInitialScroll = it[0]
            }
        }
    )
) {
    mutableStateOf(SelectCurrencyScreenStateHolder())
}
