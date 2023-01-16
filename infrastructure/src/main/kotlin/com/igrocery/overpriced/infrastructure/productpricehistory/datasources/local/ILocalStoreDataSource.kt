package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.StoreDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalStoreDataSource : IBaseLocalDataSource {

    suspend fun insertStore(storeRoomEntity: StoreRoomEntity): Long

    suspend fun updateStore(storeRoomEntity: StoreRoomEntity)

    suspend fun deleteStore(storeRoomEntity: StoreRoomEntity)

    suspend fun getStoresPaging(offset: Int, pageSize: Int): List<StoreRoomEntity>

    suspend fun getStoresWithMinMaxPricesByProductIdAndCurrencyPaging(
        productId: Long,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<StoreDao.StoreWithMinMaxPrices>

    fun getStoreById(id: Long): Flow<StoreRoomEntity?>

    fun getStoresCount(): Flow<Int>
}
