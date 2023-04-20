package com.igrocery.overpriced.presentation.productlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class ProductListScreenStateHolder() {

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    false
                )
            },
            restore = {
                ProductListScreenStateHolder(
                )
            }
        )
    }
}

@Composable
fun rememberProductListScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(ProductListScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(ProductListScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(ProductListScreenStateHolder())
}
