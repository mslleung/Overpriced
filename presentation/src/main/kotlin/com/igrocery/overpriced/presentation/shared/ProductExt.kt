package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.Composable
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import kotlin.math.ceil

@Composable
fun ProductQuantity.getDisplayString(): String {
    val displayAmount = if (amount % 1 == 0.0)
        "%d".format(amount.toInt())
    else
        "%.2f".format(amount)

    val displayUnit = unit.getDisplayString(plural = ceil(amount).toInt() > 1)
    return "$displayAmount $displayUnit"
}
