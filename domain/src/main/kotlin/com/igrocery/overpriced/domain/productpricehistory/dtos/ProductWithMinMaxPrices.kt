package com.igrocery.overpriced.domain.productpricehistory.dtos

import com.igrocery.overpriced.domain.productpricehistory.models.Product

data class ProductWithMinMaxPrices(
    val product: Product,
    val minPrice: Double?, // can be null if the product has no price record
    val maxPrice: Double?, // can be null if the product has no price record
    val lastUpdatedTimestamp: Long?, // can be null if the product has no price record
)
