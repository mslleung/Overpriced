package com.igrocery.overpriced.domain.grocerylist.models

import android.os.Parcelable
import com.igrocery.overpriced.domain.AggregateRoot
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroceryListItem(
    override val id: Long = 0,
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val groceryListId: Long,
    val productId: Long,
    val quantity: String    // quantity is very arbitrary, so we store strings for maximum flexibility
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(groceryListId > 0)
        require(productId > 0)
        require(quantity.length < 100)
    }

}
