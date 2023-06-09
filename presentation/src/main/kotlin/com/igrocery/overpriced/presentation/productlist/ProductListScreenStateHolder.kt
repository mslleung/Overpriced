package com.igrocery.overpriced.presentation.productlist

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import kotlinx.parcelize.Parcelize

class ProductListScreenStateHolder() {

//    @Parcelize
//    data class ProductMoreDialogData(
//        val product
//    ) : Parcelable

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
