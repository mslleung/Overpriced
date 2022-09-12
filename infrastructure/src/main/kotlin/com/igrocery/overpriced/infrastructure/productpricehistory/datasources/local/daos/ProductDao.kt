package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductFtsRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ProductDao : BaseDao<ProductRoomEntity> {

    @Query("SELECT * FROM products ORDER BY update_timestamp LIMIT :pageSize OFFSET :offset")
    suspend fun getProductsPage(offset: Int, pageSize: Int): List<ProductRoomEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<ProductRoomEntity?>

    @Query(
        "SELECT * FROM products " +
                "WHERE products.name = :name AND products.description = :description"
    )
    fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<ProductRoomEntity?>

    @Query(
        "SELECT * FROM products " +
                "JOIN products_fts ON products.id = products_fts.rowid " +
                "WHERE products_fts MATCH :query " +
                "ORDER BY name, description " +
                "LIMIT :pageSize OFFSET :offset "
    )
    suspend fun searchProducts(query: String, offset: Int, pageSize: Int): List<ProductRoomEntity>

    @Query("SELECT * FROM products " +
            "WHERE category_id = :categoryId OR (category_id IS NULL AND :categoryId IS NULL) " +
            "ORDER BY name, description " +
            "LIMIT :pageSize OFFSET :offset")
    fun getProductByCategoryPaging(categoryId: Long?, offset: Int, pageSize: Int): List<ProductRoomEntity>

}
