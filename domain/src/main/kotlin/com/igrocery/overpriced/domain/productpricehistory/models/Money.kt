package com.igrocery.overpriced.domain.productpricehistory.models

import java.util.*

data class Money(
    val amount: Double,
    val currency: Currency,
) {

    init {
        require(amount in 0.0..1000000.0)
    }
}
