package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SaleQuantity(
    val amount: Double,
    val unit: SaleQuantityUnit,
) : Parcelable {

    init {
        require(amount > 0.0 && amount in 0.0..1000000.0)
    }
}

/**
 * This is conceptually a multiple over the product quantity. As vendors typically sell some items
 * in bulk. e.g. They usually sell apples in pack of 5 etc.
 */
enum class SaleQuantityUnit() {
    Half,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
}
