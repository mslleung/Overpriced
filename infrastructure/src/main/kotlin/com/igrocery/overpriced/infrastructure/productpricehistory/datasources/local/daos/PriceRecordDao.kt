package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PriceRecordDao : BaseDao<PriceRecordRoomEntity> {

    @Query("SELECT * FROM price_records WHERE product_id = :productId")
    fun getPriceRecords(productId: Long): Flow<List<PriceRecordRoomEntity>>

    @Query(
        """
            SELECT price_records.* FROM price_records
            WHERE product_id = :productId
                AND store_id = :storeId
                AND currency = :currency
            ORDER BY creation_timestamp DESC
        """
    )
    fun getPriceRecordsPaging(
        productId: Long,
        storeId: Long,
        currency: String,
    ): PagingSource<Int, PriceRecordRoomEntity>

}
