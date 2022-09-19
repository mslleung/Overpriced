package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE

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
    var productId: Long,
    @ColumnInfo(name = "store_id")
    val storeId: Long,

    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "currency")
    val currency: String,
)
