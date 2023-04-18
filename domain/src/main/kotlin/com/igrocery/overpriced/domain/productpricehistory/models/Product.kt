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
    val quantity: ProductQuantity,
    val categoryId: CategoryId?,
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(name.isNotBlank())
        require(name.length <= 100)
        categoryId?.let { require(it.value != 0L) {"use null instead of 0 to indicate empty"} }
    }
}

@Parcelize
data class ProductQuantity(
    val amount: Double,
    val unit: ProductQuantityUnit
) : Parcelable {

    init {
        require(amount > 0.0 && amount in 0.0..1000000.0)
    }
}

enum class ProductQuantityUnit {
    Baskets,
    Blocks,
    Grams,
    Kilograms,
    Litres,
    MilliLitres,
    Pieces,
    Pounds,
}
