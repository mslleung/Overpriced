package com.igrocery.overpriced.domain.productpricehistory.models

import com.igrocery.overpriced.domain.AggregateRoot

data class PriceRecord(
    override val id: Long = 0,
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val productId: Long,
    val price: Money,
    val storeId: Long,
) : AggregateRoot(id, creationTimestamp, updateTimestamp) {

    init {
        require(productId > 0)
        require(storeId > 0)
    }

}
