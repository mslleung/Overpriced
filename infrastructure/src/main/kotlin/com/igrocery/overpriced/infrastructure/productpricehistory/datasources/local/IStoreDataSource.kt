package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import kotlinx.coroutines.flow.Flow

internal interface IStoreDataSource {

    fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver)

    suspend fun insertStore(storeRoomEntity: StoreRoomEntity): Long

    suspend fun updateStore(storeRoomEntity: StoreRoomEntity)

    suspend fun deleteStore(storeRoomEntity: StoreRoomEntity)

    suspend fun getStoresPage(offset: Int, pageSize: Int): List<StoreRoomEntity>

    fun getStoreById(id: Long): Flow<StoreRoomEntity?>

    fun getStoresCount(): Flow<Int>
}
