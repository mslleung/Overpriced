package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.StoreDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalStoreDataSource : IBaseLocalDataSource<StoreId, StoreRoomEntity> {

    fun getStoresPaging(): PagingSource<Int, StoreRoomEntity>

    fun getStoresWithMinMaxPricesPaging(
        productId: ProductId,
        currency: Currency,
    ): PagingSource<Int, StoreDao.StoreWithMinMaxPrices>

    fun getStore(id: StoreId): Flow<StoreRoomEntity>

    fun getStoresCount(): Flow<Int>
}
