package com.igrocery.overpriced.presentation.storepricedetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class StorePriceDetailScreenStateHolder() {


    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    false
                )
            },
            restore = {
                StorePriceDetailScreenStateHolder(
                )
            }
        )
    }

}

@Composable
fun rememberStorePriceDetailScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(StorePriceDetailScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(StorePriceDetailScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(StorePriceDetailScreenStateHolder())
}
