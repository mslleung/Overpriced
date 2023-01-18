package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.Address
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.domain.productpricehistory.models.Store

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



// mapping functions

internal fun StoreRoomEntity.toDomain(): Store {
    val address = Address(
        lines = addressLines,
        geoCoordinates = GeoCoordinates(latitude, longitude)
    )
    return Store(
        id = StoreId(id),
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        name = name,
        address = address,
    )
}

internal fun Store.toData(): StoreRoomEntity {
    return StoreRoomEntity(
        id = id.value,
        creationTimestamp = creationTimestamp,
        updateTimestamp = updateTimestamp,
        name = name,
        addressLines = address.lines,
        latitude = address.geoCoordinates.latitude,
        longitude = address.geoCoordinates.longitude,
    )
}
