package com.igrocery.overpriced.domain.productpricehistory.dtos

import com.igrocery.overpriced.domain.productpricehistory.models.Store

data class StoreWithMinMaxPrices(
    val store: Store,
    val minPrice: Double,
    val maxPrice: Double,
    val lastUpdatedTimestamp: Long,
)
