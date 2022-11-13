package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.R

/**
 * A convenience way to create a category object representing "No category".
 * This is because "No category" is usually displayed just like an actual category.
 */
val NoCategory: Category
    @Composable get() = Category(
        icon = CategoryIcon.NoCategory,
        name = stringResource(R.string.no_category)
    )
