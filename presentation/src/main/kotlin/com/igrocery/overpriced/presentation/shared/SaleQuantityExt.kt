package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.Composable
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantity

@Composable
fun SaleQuantity.getDisplayString(): String {
    return when (this) {
//        SaleQuantity.Half -> "0.5"
        SaleQuantity.One -> "1"
        SaleQuantity.Two -> "2"
        SaleQuantity.Three -> "3"
        SaleQuantity.Four -> "4"
        SaleQuantity.Five -> "5"
        SaleQuantity.Six -> "6"
        SaleQuantity.Seven -> "7"
        SaleQuantity.Eight -> "8"
//        SaleQuantity.Nine -> "9"
//        SaleQuantity.Ten -> "10"
    }
}