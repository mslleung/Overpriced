package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Clock
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private val log = Logger { }

@Singleton
internal class LocalPriceRecordDataSource @Inject internal constructor(
    private val db: AppDatabase,
) : ILocalPriceRecordDataSource {

    private val invalidationObserverDelegate = InvalidationObserverDelegate(db, "price_records")

    override fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver) {
        invalidationObserverDelegate.addWeakInvalidationObserver(invalidationObserver)
    }

    override suspend fun insertPriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity): Long {
        val time = Clock.System.now().toEpochMilliseconds()
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
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
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

    override suspend fun getPriceRecordsPaging(
        productId: Long,
        storeId: Long,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<PriceRecordRoomEntity> {
        return db.priceRecordDao()
            .getPriceRecordsPaging(productId, storeId, currency.currencyCode, offset, pageSize)
    }

}
