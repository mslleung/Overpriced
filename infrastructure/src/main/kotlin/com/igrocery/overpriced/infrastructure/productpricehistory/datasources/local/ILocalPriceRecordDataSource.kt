package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalPriceRecordDataSource : IBaseLocalDataSource {

    suspend fun insertPriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity): Long

    suspend fun updatePriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity)

    suspend fun deletePriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity)

    fun getPriceRecordsByProductId(productId: Long): Flow<List<PriceRecordRoomEntity>>

    suspend fun getPriceRecordsPaging(
        productId: Long,
        storeId: Long,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<PriceRecordRoomEntity>

}
