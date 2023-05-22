package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.PriceRecordId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.infrastructure.AppDatabase
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

    override suspend fun insert(entity: PriceRecordRoomEntity): PriceRecordId {
        val time = Clock.System.now().toEpochMilliseconds()
        val entityToInsert = entity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val id = db.priceRecordDao().insert(entityToInsert)
        require(id > 0)
        return PriceRecordId(id)
    }

    override suspend fun update(entity: PriceRecordRoomEntity) {
        val entityToUpdate = entity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.priceRecordDao().update(entityToUpdate)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(entity: PriceRecordRoomEntity) {
        val rowsDeleted = db.priceRecordDao().delete(entity)
        require(rowsDeleted == 1)
    }

    override fun getPriceRecords(productId: ProductId): Flow<List<PriceRecordRoomEntity>> {
        return db.priceRecordDao().getPriceRecords(productId.value)
            .distinctUntilChanged()
    }

    override fun getPriceRecordsPaging(
        productId: ProductId,
        storeId: StoreId,
        currency: Currency,
    ): PagingSource<Int, PriceRecordRoomEntity> {
        return db.priceRecordDao().getPriceRecordsPaging(
            productId.value,
            storeId.value,
            currency.currencyCode,
        )
    }

}
