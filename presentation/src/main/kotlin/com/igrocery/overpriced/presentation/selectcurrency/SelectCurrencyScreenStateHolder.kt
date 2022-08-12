package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.*

@Stable
data class SelectCurrencyScreenStateHolder(
    val isInitialScroll: Boolean = true,
    val availableCurrencies: List<Currency> = Currency.getAvailableCurrencies()
        .sortedBy {
            it.currencyCode
        },
)

@Composable
fun rememberSelectCurrencyScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.isInitialScroll
            )
        },
        restore = {
            SelectCurrencyScreenStateHolder(
                isInitialScroll = it[0]
            )
        }
    )
) {
    mutableStateOf(SelectCurrencyScreenStateHolder())
}
