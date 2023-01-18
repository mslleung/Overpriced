package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.domain.PriceRecordId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalPriceRecordDataSource : IBaseLocalDataSource<PriceRecordId, PriceRecordRoomEntity> {

    fun getPriceRecords(productId: ProductId): Flow<List<PriceRecordRoomEntity>>

    suspend fun getPriceRecordsPaging(
        productId: ProductId,
        storeId: StoreId,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<PriceRecordRoomEntity>

}
