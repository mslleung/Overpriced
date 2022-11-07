package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.igrocery.overpriced.domain.productpricehistory.models.Money
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import java.util.*

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
)



// mapping functions

internal fun PriceRecordRoomEntity.toDomain(): PriceRecord {
    return PriceRecord(
        id = id,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        productId = productId,
        price = Money(
            amount = price,
            currency = Currency.getInstance(currency)
        ),
        storeId = storeId,
    )
}

internal fun PriceRecord.toData(): PriceRecordRoomEntity {
    return PriceRecordRoomEntity(
        id = id,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        productId = productId,
        price = price.amount,
        currency = price.currency.currencyCode,
        storeId = storeId,
    )
}
