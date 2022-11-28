package com.igrocery.overpriced.presentation.productdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class ProductDetailScreenStateHolder(savedState: List<*>? = null) {

    var isLazyListPagingFirstLoad by mutableStateOf(savedState?.get(0) as? Boolean ?: true)

}

@Composable
fun rememberProductDetailScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.isLazyListPagingFirstLoad,
            )
        },
        restore = { savedState ->
            ProductDetailScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(ProductDetailScreenStateHolder())
}