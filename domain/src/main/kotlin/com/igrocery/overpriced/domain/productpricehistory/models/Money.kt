package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Money(
    val amount: Double,
    val currency: Currency,
) : Parcelable {

    init {
        require(amount in 0.0..1000000.0)
    }
}
