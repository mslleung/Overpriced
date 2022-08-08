package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryDao : BaseDao<CategoryRoomEntity> {

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<CategoryRoomEntity>

    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<CategoryRoomEntity>>  // no paging, it is likely going to be small

    // right join is not supported
    // we need to use a left join so products without category can still be counted
    @MapInfo(valueColumn = "productCount")
    @Query("SELECT categories.*, COUNT(products.category_id) AS productCount FROM products " +
            "LEFT JOIN categories ON products.category_id = categories.id " +
            "GROUP BY products.category_id")
    fun getCategoryWithProductCount(): Flow<Map<CategoryRoomEntity?, Int>>

}
