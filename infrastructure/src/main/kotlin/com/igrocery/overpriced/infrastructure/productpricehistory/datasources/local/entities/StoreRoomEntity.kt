package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
internal data class StoreRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,
    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: Long,

    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "address_lines")
    val addressLines: String?,
    @ColumnInfo(name = "address_latitude")
    val latitude: Double,
    @ColumnInfo(name = "address_longitude")
    val longitude: Double,
)
