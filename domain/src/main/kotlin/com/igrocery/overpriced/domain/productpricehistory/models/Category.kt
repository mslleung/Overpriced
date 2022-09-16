package com.igrocery.overpriced.domain.productpricehistory.models

import androidx.core.text.trimmedLength
import com.igrocery.overpriced.domain.AggregateRoot

data class Category(
    override val id: Long = 0,
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val icon: CategoryIcon,
    val name: String,
) : AggregateRoot(id, creationTimestamp, updateTimestamp) {

    init {
        require(name.trimmedLength() == name.length)
        require(name.isNotBlank())
        require(name.length <= 100)
    }

}
