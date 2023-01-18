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
    val storeId: StoreId,
) : AggregateRoot(id, creationTimestamp, updateTimestamp), Parcelable {

    init {
        require(productId.value > 0)
        require(storeId.value > 0)
    }

}
