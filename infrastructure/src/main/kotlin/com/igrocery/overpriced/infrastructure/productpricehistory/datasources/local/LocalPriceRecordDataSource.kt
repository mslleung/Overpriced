package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalPriceRecordDataSource @Inject internal constructor(
    private val db: AppDatabase,
) : ILocalPriceRecordDataSource {

    override suspend fun insertPriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity): Long {
        val time = System.nanoTime()
        val entity = priceRecordRoomEntity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val id = db.priceRecordDao().insert(entity)
        require(id > 0)
        return id
    }

    override suspend fun updatePriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity) {
        val entity = priceRecordRoomEntity.copy(
            updateTimestamp = System.nanoTime()
        )

        val rowsUpdated = db.priceRecordDao().update(entity)
        require(rowsUpdated == 1)
    }

    override suspend fun deletePriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity) {
        val rowsDeleted = db.priceRecordDao().delete(priceRecordRoomEntity)
        require(rowsDeleted == 1)
    }

    override fun getPriceRecordsByProductId(productId: Long): Flow<List<PriceRecordRoomEntity>> {
        return db.priceRecordDao().getPriceRecordsByProductId(productId)
            .distinctUntilChanged()
    }

}
