package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.room.Dao
import androidx.room.Query
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryDao : BaseDao<CategoryRoomEntity> {

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<CategoryRoomEntity>

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryRoomEntity>>  // no paging, it is likely going to be small

}
