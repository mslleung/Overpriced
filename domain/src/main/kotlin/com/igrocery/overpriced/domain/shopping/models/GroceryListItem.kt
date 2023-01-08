package com.igrocery.overpriced.domain.shopping.models

import android.os.Parcelable
import com.igrocery.overpriced.domain.AggregateRoot
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroceryListItem(
    override val id: Long = 0,
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val productId: Long,
    val quantity: Long
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(productId > 0)
        require(quantity in 1..98)
    }

}
