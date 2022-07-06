package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true),
    ]
)
internal data class CategoryRoomEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "name")
    val name: String,
)
