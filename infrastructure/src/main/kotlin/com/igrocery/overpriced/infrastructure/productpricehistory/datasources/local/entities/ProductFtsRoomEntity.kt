package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "products_fts")
@Fts4(contentEntity = ProductRoomEntity::class)
internal data class ProductFtsRoomEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowId: Int,

    @ColumnInfo(name = "name")
    val name: String,
)
