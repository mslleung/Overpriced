package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
)
internal data class CategoryRoomEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,

    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "name")
    val name: String,
)
