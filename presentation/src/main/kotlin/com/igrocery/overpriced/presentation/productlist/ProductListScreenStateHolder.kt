package com.igrocery.overpriced.presentation.productlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class ProductListScreenStateHolder {

    var isLazyListPagingFirstLoad by mutableStateOf(true)

    companion object {
        val Saver : Saver<ProductListScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isLazyListPagingFirstLoad,
                )
            },
            restore = {
                ProductListScreenStateHolder().apply {
                    isLazyListPagingFirstLoad = it[0] as Boolean
                }
            }
        )
    }
}

@Composable
fun rememberProductListScreenState() = rememberSaveable(
    stateSaver = ProductListScreenStateHolder.Saver
) {
    // UiState is not designed to be mutable. It should NEVER be reassigned.
    // The only exception is activity config change and process recreation. Hence it is mutable.
    mutableStateOf(ProductListScreenStateHolder())
}
