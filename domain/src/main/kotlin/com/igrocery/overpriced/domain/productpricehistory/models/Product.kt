package com.igrocery.overpriced.domain.productpricehistory.models

import com.igrocery.overpriced.domain.AggregateRoot

data class Product(
    override val id: Long = 0,
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val name: String,
    val description: String,   // the product brand, weight/size/flavor etc.
    val categoryId: Long?,
) : AggregateRoot(id, creationTimestamp, updateTimestamp) {

    init {
        require(name.isNotBlank())
        require(name.length <= 100)
        require(description.length <= 100)
    }

}
