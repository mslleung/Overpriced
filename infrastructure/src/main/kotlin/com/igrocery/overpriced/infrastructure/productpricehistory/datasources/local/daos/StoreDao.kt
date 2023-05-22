package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
internal interface StoreDao: BaseDao<StoreRoomEntity> {

    @Query("SELECT * FROM stores ORDER BY name, creation_timestamp")
    fun getStoresPaging(): PagingSource<Int, StoreRoomEntity>

    @Query("SELECT * FROM stores WHERE id = :id")
    fun getStoreById(id: Long) : Flow<StoreRoomEntity>

    @Query("SELECT COUNT(id) FROM stores")
    fun getStoresCount() : Flow<Int>

    @Query(
        """
            SELECT stores.*,
                MIN(price_records.price / price_records.quantity) AS minPrice,
                MAX(price_records.price / price_records.quantity) AS maxPrice,
                MAX(price_records.update_timestamp) AS lastUpdatedTimestamp
            FROM stores
            INNER JOIN price_records ON price_records.store_id = stores.id
            WHERE price_records.product_id = :productId AND price_records.currency = :currency
            GROUP BY stores.id
            ORDER BY stores.name, stores.address_lines
        """
    )
    fun getStoresWithMinMaxPricesPaging(
        productId: Long,
        currency: String,
    ): PagingSource<Int, StoreWithMinMaxPrices>

    data class StoreWithMinMaxPrices(
        @Embedded val storeRoomEntity: StoreRoomEntity,
        val minPrice: Double,
        val maxPrice: Double,
        val lastUpdatedTimestamp: Long,
    )

}
