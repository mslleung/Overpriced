package com.igrocery.overpriced.presentation.shared

import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity

fun ProductQuantity.getDisplayString(): String {
    return "$amount ${unit.name}"
}
