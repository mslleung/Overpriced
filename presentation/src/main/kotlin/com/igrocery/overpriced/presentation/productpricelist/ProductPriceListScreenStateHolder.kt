package com.igrocery.overpriced.presentation.productpricelist

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

class ProductPriceListScreenStateHolder {

    var isLazyListPagingFirstLoad = true

    companion object {
        val Saver = Saver<ProductPriceListScreenStateHolder, Bundle>(
            save = {
                Bundle().apply {
//                    putBoolean(KEY_IS_LAZY_LIST_PAGING_FIRST_LOAD, it.isLazyListPagingFirstLoad)
                }
            },
            restore = { bundle ->
                ProductPriceListScreenStateHolder().apply {
//                    isLazyListPagingFirstLoad = bundle.getBoolean(KEY_IS_LAZY_LIST_PAGING_FIRST_LOAD)
                }
            }
        )
    }
}

@Composable
fun rememberProductPriceListScreenState() = rememberSaveable(
    stateSaver = ProductPriceListScreenStateHolder.Saver
) {
    // UiState is not designed to be mutable. It should NEVER be reassigned.
    // The only exception is activity config change and process recreation. Hence it is mutable.
    mutableStateOf(ProductPriceListScreenStateHolder())
}
