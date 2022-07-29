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

    @Query("SELECT * FROM products WHERE products.barcode = :barcode ")
    fun getProductByBarcode(barcode: String): Flow<ProductRoomEntity?>

    @Query(
        "SELECT * FROM products " +
                "JOIN products_fts ON products.id = products_fts.rowid " +
                "WHERE products_fts MATCH :query " +
                "ORDER BY update_timestamp " +
                "LIMIT :pageSize OFFSET :offset "
    )
    suspend fun searchProducts(query: String, offset: Int, pageSize: Int): List<ProductRoomEntity>

    @Query("SELECT COUNT(id) FROM products WHERE category_id = :categoryId")
    fun getProductCountWithCategory(categoryId: Long): Flow<Int>

    @Query("SELECT COUNT(id) FROM products WHERE category_id IS NULL")
    fun getProductCountWithNoCategory(): Flow<Int>

}
