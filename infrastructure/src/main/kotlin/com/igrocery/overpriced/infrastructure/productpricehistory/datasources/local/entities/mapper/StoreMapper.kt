package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper

import com.igrocery.overpriced.domain.productpricehistory.models.Address
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StoreMapper @Inject constructor() {

    fun mapToData(store: Store): StoreRoomEntity {
        return StoreRoomEntity(
            id = store.id,
            name = store.name,
            addressLines = store.address.lines,
            latitude = store.address.geoCoordinates.latitude,
            longitude = store.address.geoCoordinates.longitude,
            creationTimestamp = store.creationTimestamp,
            updateTimestamp = store.updateTimestamp,
        )
    }

    fun mapFromData(storeRoomEntity: StoreRoomEntity): Store {
        val address = Address(
            lines = storeRoomEntity.addressLines,
            geoCoordinates = GeoCoordinates(storeRoomEntity.latitude, storeRoomEntity.longitude)
        )
        return Store(
            id = storeRoomEntity.id,
            name = storeRoomEntity.name,
            address = address,
            creationTimestamp = storeRoomEntity.creationTimestamp,
            updateTimestamp = storeRoomEntity.updateTimestamp,
        )
    }

}
