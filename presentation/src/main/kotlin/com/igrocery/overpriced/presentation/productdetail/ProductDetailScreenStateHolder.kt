package com.igrocery.overpriced.presentation.productdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class ProductDetailScreenStateHolder() {

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    false
                )
            },
            restore = {
                ProductDetailScreenStateHolder(

                )
            }
        )
    }
}

@Composable
fun rememberProductDetailScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(ProductDetailScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(ProductDetailScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(ProductDetailScreenStateHolder())
}