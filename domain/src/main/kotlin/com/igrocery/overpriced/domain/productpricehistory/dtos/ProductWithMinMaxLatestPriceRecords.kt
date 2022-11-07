package com.igrocery.overpriced.domain.productpricehistory.dtos

import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.domain.productpricehistory.models.Product

data class ProductWithMinMaxLatestPriceRecords(
    val product: Product,
    val minPriceRecord: PriceRecord?, // can be null if the product has no price record
    val maxPriceRecord: PriceRecord?, // can be null if the product has no price record
    val latestPriceRecord: PriceRecord?, // can be null if the product has no price record
)
