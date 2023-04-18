package com.igrocery.overpriced.domain.productpricehistory.models

import android.os.Parcelable
import com.igrocery.overpriced.domain.AggregateRoot
import com.igrocery.overpriced.domain.PriceRecordId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import kotlinx.parcelize.Parcelize

@Parcelize
data class PriceRecord(
    override val id: PriceRecordId = PriceRecordId(0),
    override val creationTimestamp: Long = 0,
    override val updateTimestamp: Long = 0,
    val productId: ProductId,
    val price: Money,
    val quantity: SaleQuantity,
    val isSale: Boolean,
    val storeId: StoreId,
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(productId.value > 0)
        require(storeId.value > 0)
    }
}

/**
 * This is conceptually a multiple over the product quantity. As vendors typically sell some items
 * in bulk. e.g. They usually sell apples in pack of 5 etc.
 */
enum class SaleQuantity {
    Half,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
}
