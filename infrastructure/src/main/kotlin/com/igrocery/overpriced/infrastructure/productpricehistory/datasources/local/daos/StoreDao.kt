package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.room.Dao
import androidx.room.Query
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.StoreRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StoreDao: BaseDao<StoreRoomEntity> {

    @Query("SELECT * FROM stores ORDER BY creation_timestamp LIMIT :pageSize OFFSET :offset")
    fun getStoresPage(offset: Int, pageSize: Int): List<StoreRoomEntity>

    @Query("SELECT * FROM stores WHERE id = :id")
    fun getStoreById(id: Long) : Flow<StoreRoomEntity?>

    @Query("SELECT COUNT(id) FROM stores")
    fun getStoresCount() : Flow<Int>

}
