package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.*

data class SelectCurrencyScreenStateHolder(
    val isInitialScroll: Boolean = true,
    val availableCurrencies: List<Currency> = Currency.getAvailableCurrencies().sortedBy {
        it.currencyCode
    }
) {
    companion object {
        val Saver: Saver<SelectCurrencyScreenStateHolder, *> = listSaver(
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
    }
}

@Composable
fun rememberSelectCurrencyScreenState() = rememberSaveable(
    stateSaver = SelectCurrencyScreenStateHolder.Saver
) {
    mutableStateOf(SelectCurrencyScreenStateHolder())
}
