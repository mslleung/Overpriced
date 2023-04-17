package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantityUnit
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantityUnit.*
import com.igrocery.overpriced.presentation.R

@Composable
fun SaleQuantityUnit.getDisplayString(): String {
    return when(this) {
        Pieces -> stringResource(id = R.string.price_record_unit_pieces)
        Pounds -> stringResource(id = R.string.price_record_unit_pounds)
        Grams -> stringResource(id = R.string.price_record_unit_grams)
        Kilograms -> stringResource(id = R.string.price_record_unit_kilograms)
        Litres -> stringResource(id = R.string.price_record_unit_litres)
        MilliLitres -> stringResource(id = R.string.price_record_unit_millilitres)
    }
}

@Composable
fun SaleQuantityUnit.getShortDisplayString(): String {
    return when(this) {
        Pieces -> stringResource(id = R.string.price_record_unit_pieces_abbrev)
        Pounds -> stringResource(id = R.string.price_record_unit_pounds_abbrev)
        Grams -> stringResource(id = R.string.price_record_unit_grams_abbrev)
        Kilograms -> stringResource(id = R.string.price_record_unit_kilograms_abbrev)
        Litres -> stringResource(id = R.string.price_record_unit_litres_abbrev)
        MilliLitres -> stringResource(id = R.string.price_record_unit_millilitres_abbrev)
    }
}
