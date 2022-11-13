package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalStoreDataSource @Inject constructor(
    private val db: AppDatabase,
) : IStoreDataSource {

    private val invalidationObserverDelegate = InvalidationObserverDelegate(db, "stores")

    override fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver) {
        invalidationObserverDelegate.addWeakInvalidationObserver(invalidationObserver)
    }

    override suspend fun insertStore(storeRoomEntity: StoreRoomEntity): Long {
        val time = System.nanoTime()
        val entity = storeRoomEntity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.storeDao().insert(entity)
        require(rowId > 0)
        return rowId
    }

    override suspend fun updateStore(storeRoomEntity: StoreRoomEntity) {
        val entity = storeRoomEntity.copy(
            updateTimestamp = System.nanoTime()
        )

        val rowsUpdated = db.storeDao().update(entity)
        require(rowsUpdated == 1)
    }

    override suspend fun deleteStore(storeRoomEntity: StoreRoomEntity) {
        val rowsDeleted = db.storeDao().delete(storeRoomEntity)
        require(rowsDeleted == 1)
    }

    override suspend fun getStoresPage(offset: Int, pageSize: Int): List<StoreRoomEntity> {
        return db.storeDao().getStoresPage(offset, pageSize)
    }

    override fun getStoreById(id: Long): Flow<StoreRoomEntity?> {
        return db.storeDao().getStoreById(id).distinctUntilChanged()
    }

    override fun getStoresCount(): Flow<Int> {
        return db.storeDao().getStoresCount()
    }

}
