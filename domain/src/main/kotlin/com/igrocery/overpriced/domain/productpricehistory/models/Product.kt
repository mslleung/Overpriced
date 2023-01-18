package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import com.igrocery.overpriced.domain.AggregateRoot
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    override val id: ProductId = ProductId(0),
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val name: String,
    val description: String,   // the product brand, weight/size/flavor etc.
    val categoryId: CategoryId?,
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(name.isNotBlank())
        require(name.length <= 100)
        require(description.length <= 100)
        categoryId?.let { require(it.value != 0L) {"use null instead of 0 to indicate empty"} }
    }

}
