package com.igrocery.overpriced.domain.grocerylist.models

import android.os.Parcelable
import com.igrocery.overpriced.domain.AggregateRoot
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroceryList(
    override val id: Long = 0,
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val name: String,
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(name.isNotBlank())
        require(name.length <= 100)
    }

}
