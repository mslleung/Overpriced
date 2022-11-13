package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import com.igrocery.overpriced.domain.AggregateRoot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Store(
    override val id: Long = 0,
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val name: String,
    val address: Address,
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(name.isNotBlank())
        require(name.length <= 100)
    }

}
