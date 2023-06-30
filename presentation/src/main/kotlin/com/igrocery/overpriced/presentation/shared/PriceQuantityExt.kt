package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.Baskets
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.Blocks
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.Grams
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.Kilograms
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.Litres
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.MilliLitres
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.Pieces
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantityUnit.Pounds
import com.igrocery.overpriced.presentation.R

@Composable
fun ProductQuantityUnit.getDisplayString(plural: Boolean = true): String {
    val count = if (plural) 2 else 1
    return when(this) {
        Baskets -> pluralStringResource(id = R.plurals.price_quantity_unit_baskets, count = count)
        Blocks -> pluralStringResource(id = R.plurals.price_quantity_unit_blocks, count = count)
        Grams -> pluralStringResource(id = R.plurals.price_quantity_unit_grams, count = count)
        Kilograms -> pluralStringResource(id = R.plurals.price_quantity_unit_kilograms, count = count)
        Litres -> pluralStringResource(id = R.plurals.price_quantity_unit_litres, count = count)
        MilliLitres -> pluralStringResource(id = R.plurals.price_quantity_unit_millilitres, count = count)
        Pieces -> pluralStringResource(id = R.plurals.price_quantity_unit_pieces, count = count)
        Pounds -> pluralStringResource(id = R.plurals.price_quantity_unit_pounds, count = count)
    }
}

@Composable
fun ProductQuantityUnit.getShortDisplayString(): String {
    return when(this) {
        Baskets -> stringResource(id = R.string.price_quantity_unit_baskets_abbrev)
        Blocks -> stringResource(id = R.string.price_quantity_unit_blocks_abbrev)
        Grams -> stringResource(id = R.string.price_quantity_unit_grams_abbrev)
        Kilograms -> stringResource(id = R.string.price_quantity_unit_kilograms_abbrev)
        Litres -> stringResource(id = R.string.price_quantity_unit_litres_abbrev)
        MilliLitres -> stringResource(id = R.string.price_quantity_unit_millilitres_abbrev)
        Pieces -> stringResource(id = R.string.price_quantity_unit_pieces_abbrev)
        Pounds -> stringResource(id = R.string.price_quantity_unit_pounds_abbrev)
    }
}
