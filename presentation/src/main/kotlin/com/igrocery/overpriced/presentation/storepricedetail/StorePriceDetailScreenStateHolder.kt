package com.igrocery.overpriced.presentation.storepricedetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class StorePriceDetailScreenStateHolder(savedState: List<*>? = null) {

}

@Composable
fun rememberStorePriceDetailScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                false,
            )
        },
        restore = { savedState ->
            StorePriceDetailScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(StorePriceDetailScreenStateHolder())
}
