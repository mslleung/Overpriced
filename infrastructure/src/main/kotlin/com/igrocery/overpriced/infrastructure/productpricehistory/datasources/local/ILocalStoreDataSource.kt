package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.StoreDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalStoreDataSource : IBaseLocalDataSource<StoreId, StoreRoomEntity> {

    suspend fun getStoresPaging(offset: Int, pageSize: Int): List<StoreRoomEntity>

    suspend fun getStoresWithMinMaxPricesPaging(
        productId: ProductId,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<StoreDao.StoreWithMinMaxPrices>

    fun getStore(id: StoreId): Flow<StoreRoomEntity?>

    fun getStoresCount(): Flow<Int>
}
