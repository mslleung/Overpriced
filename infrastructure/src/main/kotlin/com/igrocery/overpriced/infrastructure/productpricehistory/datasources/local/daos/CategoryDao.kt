package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.room.Dao
import androidx.room.Embedded
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
    @Query(
        """
            SELECT categories.*, COUNT(case when products.category_id IS NULL then 1 else 1 end) AS productCount
            FROM products LEFT JOIN categories ON products.category_id = categories.id
            GROUP BY products.category_id
            ORDER BY categories.name
        """
    )
    fun getCategoryWithProductCount(): Flow<List<CategoryWithProductCount>>

    data class CategoryWithProductCount(
        @Embedded val categoryRoomEntity: CategoryRoomEntity?,
        val productCount: Int
    )

}
