package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.*

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            CategoryRoomEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category_id"),
            onDelete = ForeignKey.SET_DEFAULT
        ),
    ],
    indices = [
        Index(value = ["name", "description"], unique = true),
        Index(value = ["barcode"], unique = true)
    ]
)
internal data class ProductRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "barcode")
    val barcode: String?,
    @ColumnInfo(name = "category_id", defaultValue = "0")
    val categoryId: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,
)
