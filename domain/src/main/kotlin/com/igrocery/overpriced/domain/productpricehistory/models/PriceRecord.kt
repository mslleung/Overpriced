package com.igrocery.overpriced.domain.productpricehistory.models

import com.igrocery.overpriced.domain.AggregateRoot

class PriceRecord : AggregateRoot {

    class InvalidProductIdException: IllegalArgumentException("Invalid product id.")
    class InvalidStoreIdException: IllegalArgumentException("Invalid store id.")

    constructor(
        id: Long = 0,
        creationTimestamp: Long = 0,
        updateTimestamp: Long = 0,
        productId: Long,
        price: Money,
        storeId: Long,

    ): super(id, creationTimestamp, updateTimestamp) {
        this.productId = productId
        this.price = price
        this.storeId = storeId
    }

    var productId: Long
        set(value) {
            if (value <= 0) throw InvalidProductIdException()
            field = value
        }

    var price: Money

    var storeId: Long
        set(value) {
            if (value <= 0) throw InvalidStoreIdException()
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PriceRecord

        if (productId != other.productId) return false
        if (price != other.price) return false
        if (storeId != other.storeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = productId.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + storeId.hashCode()
        return result
    }

}
