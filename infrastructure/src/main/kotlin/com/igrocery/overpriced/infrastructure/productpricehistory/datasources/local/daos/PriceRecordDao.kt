package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.room.*
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PriceRecordDao : BaseDao<PriceRecordRoomEntity> {

    @Query("SELECT * FROM price_records WHERE product_id = :productId")
    fun getPriceRecordsByProductId(productId: Long): Flow<List<PriceRecordRoomEntity>>

}
