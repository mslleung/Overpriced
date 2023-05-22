package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.StoreDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
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
internal class LocalStoreDataSource @Inject constructor(
    private val db: AppDatabase,
) : ILocalStoreDataSource {

    override suspend fun insert(entity: StoreRoomEntity): StoreId {
        val time = Clock.System.now().toEpochMilliseconds()
        val entityToInsert = entity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.storeDao().insert(entityToInsert)
        require(rowId > 0)
        return StoreId(rowId)
    }

    override suspend fun update(entity: StoreRoomEntity) {
        val entityToUpdate = entity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.storeDao().update(entityToUpdate)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(entity: StoreRoomEntity) {
        val rowsDeleted = db.storeDao().delete(entity)
        require(rowsDeleted == 1)
    }

    override fun getStoresPaging(): PagingSource<Int, StoreRoomEntity> {
        return db.storeDao().getStoresPaging()
    }

    override fun getStoresWithMinMaxPricesPaging(
        productId: ProductId,
        currency: Currency,
    ): PagingSource<Int, StoreDao.StoreWithMinMaxPrices> {
        return db.storeDao().getStoresWithMinMaxPricesPaging(
            productId.value,
            currency.currencyCode,
        )
    }

    override fun getStore(id: StoreId): Flow<StoreRoomEntity> {
        return db.storeDao().getStoreById(id.value).distinctUntilChanged()
    }

    override fun getStoresCount(): Flow<Int> {
        return db.storeDao().getStoresCount()
    }

}
