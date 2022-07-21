package com.igrocery.overpriced.domain.productpricehistory.models

import androidx.annotation.DrawableRes
import com.igrocery.overpriced.domain.R

enum class CategoryIcon(
    @DrawableRes val iconRes: Int,
) {
    Uncategorized(R.drawable.ic_question_svgrepo_com),
    Vegetables(R.drawable.ic_carrot_svgrepo_com),
    Fruits(R.drawable.ic_apple_svgrepo_com),
    Meat(R.drawable.ic_steak_svgrepo_com),
    Dairy(R.drawable.ic_milk_svgrepo_com),
    Grains(R.drawable.ic_toast_svgrepo_com),
    Beverages(R.drawable.ic_can_svgrepo_com),
    Seasonings(R.drawable.ic_spices_svgrepo_com),
    Snacks(R.drawable.ic_chocolate_svgrepo_com),
    ToolsAndUtensils(R.drawable.ic_cutlery_svgrepo_com),
}
