package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import kotlinx.coroutines.flow.Flow

internal interface ILocalPriceRecordDataSource {

    suspend fun insertPriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity): Long

    suspend fun updatePriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity)

    suspend fun deletePriceRecord(priceRecordRoomEntity: PriceRecordRoomEntity)

    fun getPriceRecordsByProductId(productId: Long): Flow<List<PriceRecordRoomEntity>>

}
