package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantity

@Composable
fun SaleQuantity.getDisplayString(): String {
    return when (this) {
        SaleQuantity.Half -> "0.5"
        SaleQuantity.One -> "1"
        SaleQuantity.Two -> "2"
        SaleQuantity.Three -> "3"
        SaleQuantity.Four -> "4"
        SaleQuantity.Five -> TODO()
        SaleQuantity.Six -> TODO()
        SaleQuantity.Seven -> TODO()
        SaleQuantity.Eight -> TODO()
        SaleQuantity.Nine -> TODO()
        SaleQuantity.Ten -> TODO()
    }
}