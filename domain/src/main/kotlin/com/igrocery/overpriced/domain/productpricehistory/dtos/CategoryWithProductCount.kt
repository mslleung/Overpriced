package com.igrocery.overpriced.domain.productpricehistory.dtos

import com.igrocery.overpriced.domain.productpricehistory.models.Category

data class CategoryWithProductCount(
    val category: Category?,
    val productCount: Int
)
