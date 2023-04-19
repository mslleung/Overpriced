package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.igrocery.overpriced.domain.PriceRecordId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.Money
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantity
import java.util.Currency

@Entity(
    tableName = "price_records",
    foreignKeys = [
        ForeignKey(
            ProductRoomEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("product_id"),
            onDelete = CASCADE
        ),
        ForeignKey(
            StoreRoomEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("store_id"),
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["product_id"]),
        Index(value = ["store_id"])
    ]
)
internal data class PriceRecordRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,

    @ColumnInfo(name = "product_id")
    val productId: Long,
    @ColumnInfo(name = "store_id")
    val storeId: Long,

    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "currency")
    val currency: String,

    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @ColumnInfo(name = "is_sale")
    val isSale: Boolean,
)



// mapping functions

internal fun PriceRecordRoomEntity.toDomain(): PriceRecord {
    return PriceRecord(
        id = PriceRecordId(id),
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        productId = ProductId(productId),
        price = Money(
            amount = price,
            currency = Currency.getInstance(currency)
        ),
        quantity = SaleQuantity.values().find { it.numeric == quantity }!!,
        storeId = StoreId(storeId),
        isSale = isSale
    )
}

internal fun PriceRecord.toData(): PriceRecordRoomEntity {
    return PriceRecordRoomEntity(
        id = id.value,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        productId = productId.value,
        price = price.amount,
        currency = price.currency.currencyCode,
        quantity = quantity.numeric,
        isSale = isSale,
        storeId = storeId.value,
    )
}
