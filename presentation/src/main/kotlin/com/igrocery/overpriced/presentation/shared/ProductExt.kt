package com.igrocery.overpriced.presentation.shared

import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity

fun ProductQuantity.getDisplayString(): String {
    val displayAmount = if (amount % 1 == 0.0)
        "%d".format(amount.toInt())
    else
        "%.2f".format(amount)
    return "$displayAmount ${unit.name}"
}
