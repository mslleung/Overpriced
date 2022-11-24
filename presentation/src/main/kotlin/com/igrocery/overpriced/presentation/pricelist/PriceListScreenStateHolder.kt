package com.igrocery.overpriced.presentation.pricelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class PriceListScreenStateHolder(savedState: List<*>? = null) {

    var isLazyListPagingFirstLoad by mutableStateOf(savedState?.get(0) as? Boolean ?: true)

}

@Composable
fun rememberPriceListScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.isLazyListPagingFirstLoad,
            )
        },
        restore = { savedState ->
            PriceListScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(PriceListScreenStateHolder())
}