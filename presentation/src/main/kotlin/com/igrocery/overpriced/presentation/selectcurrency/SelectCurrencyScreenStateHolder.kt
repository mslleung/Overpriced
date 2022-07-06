package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.util.*

class SelectCurrencyScreenStateHolder {

    var isInitialScroll by mutableStateOf(true)

    val availableCurrencies = Currency.getAvailableCurrencies().sortedBy {
        it.currencyCode
    }

    companion object {
        val Saver: Saver<SelectCurrencyScreenStateHolder, *> = listSaver(
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
    }
}

@Composable
fun rememberSelectCurrencyScreenState() = rememberSaveable(
    stateSaver = SelectCurrencyScreenStateHolder.Saver
) {
    mutableStateOf(SelectCurrencyScreenStateHolder())
}
