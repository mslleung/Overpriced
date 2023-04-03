package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PriceQuantity(
    val amount: Double,
    val unit: PriceQuantityUnit,
) : Parcelable {

    init {
        require(amount > 0)
    }
}

enum class PriceQuantityUnit {
    Pieces,
    Pounds,
    Grams,
    Kilograms,
    Litres,
    MilliLitres
}
