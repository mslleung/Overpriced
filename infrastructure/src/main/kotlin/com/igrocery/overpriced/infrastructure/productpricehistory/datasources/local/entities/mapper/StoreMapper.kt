package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper

import com.igrocery.overpriced.domain.productpricehistory.models.Address
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import javax.inject.Inject
import javax.inject.Singleton

internal class StoreMapper {

    fun mapToData(store: Store): StoreRoomEntity {
        return StoreRoomEntity(
            id = store.id,
            creationTimestamp = store.creationTimestamp,
            updateTimestamp = store.updateTimestamp,
            name = store.name,
            addressLines = store.address.lines,
            latitude = store.address.geoCoordinates.latitude,
            longitude = store.address.geoCoordinates.longitude,
        )
    }

    fun mapFromData(storeRoomEntity: StoreRoomEntity): Store {
        val address = Address(
            lines = storeRoomEntity.addressLines,
            geoCoordinates = GeoCoordinates(storeRoomEntity.latitude, storeRoomEntity.longitude)
        )
        return Store(
            id = storeRoomEntity.id,
            creationTimestamp = storeRoomEntity.creationTimestamp,
            updateTimestamp = storeRoomEntity.updateTimestamp,
            name = storeRoomEntity.name,
            address = address,
        )
    }

}
