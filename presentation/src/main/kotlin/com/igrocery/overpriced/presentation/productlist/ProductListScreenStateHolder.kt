package com.igrocery.overpriced.presentation.productlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class ProductListScreenStateHolder(savedState: List<*>? = null) {

//    var isLazyListPagingFirstLoad by mutableStateOf(savedState?.get(0) as? Boolean ?: true)

}

@Composable
fun rememberProductListScreenState() = rememberSaveable(
//    stateSaver = listSaver(
//        save = {
//            listOf(
//                it.isLazyListPagingFirstLoad,
//            )
//        },
//        restore = { savedState ->
//            ProductListScreenStateHolder(savedState)
//        }
//    )
) {
    mutableStateOf(ProductListScreenStateHolder())
}
