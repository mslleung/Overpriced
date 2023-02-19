package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.util.*

class SelectCurrencyScreenStateHolder(
    isInitialScroll: Boolean,
) {

    var isInitialScroll by mutableStateOf(isInitialScroll)
    val availableCurrencies: List<Currency> = Currency.getAvailableCurrencies()
        .sortedBy {
            it.currencyCode
        }

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isInitialScroll,
                )
            },
            restore = {
                SelectCurrencyScreenStateHolder(
                    it[0] as Boolean,
                )
            }
        )
    }

}

@Composable
fun rememberSelectCurrencyScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(SelectCurrencyScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(SelectCurrencyScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(
        SelectCurrencyScreenStateHolder(
            isInitialScroll = true,
        )
    )
}
