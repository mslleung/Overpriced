package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.room.*
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ProductDao : BaseDao<ProductRoomEntity> {

    @Query("SELECT * FROM products ORDER BY update_timestamp LIMIT :pageSize OFFSET :offset")
    suspend fun getProductsPage(offset: Int, pageSize: Int): List<ProductRoomEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<ProductRoomEntity?>

    @Query(
        """
            SELECT * FROM products
            WHERE products.name = :name AND products.description = :description
        """
    )
    fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<ProductRoomEntity?>

    @Query(
        """
            SELECT * FROM products
            JOIN products_fts ON products.id = products_fts.rowid
            WHERE products_fts MATCH :query
            ORDER BY name, description
            LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun searchProductsPaging(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity>

    @Query(
        """
            SELECT * FROM products
            WHERE category_id = :categoryId OR (category_id IS NULL AND :categoryId IS NULL)
            ORDER BY name, description LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getProductByCategoryPaging(
        categoryId: Long?,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity>

    @Query(
//        """
//            SELECT products.*, min_table.* AS minPriceRecord, max_table.* AS maxPriceRecord, latest_table.* AS latestPriceRecord
//            FROM products
//            LEFT JOIN
//            (
//                SELECT *, MIN(price) FROM price_records GROUP BY product_id
//            ) min_table ON products.id = min_table.product_id
//            LEFT JOIN
//            (
//                SELECT *, MAX(price) FROM price_records GROUP BY product_id
//            ) max_table ON products.id = max_table.product_id
//            LEFT JOIN
//            (
//                SELECT *, MAX(update_timestamp) FROM price_records GROUP BY product_id
//            ) latest_table ON products.id = latest_table.product_id
//            WHERE products.category_id = :categoryId
//            ORDER BY name, description LIMIT :pageSize OFFSET :offset
//        """
        """
            SELECT products.*, min_table.*
            FROM products LEFT JOIN price_records  ON products.id = price_records.product_id
            WHERE products.category_id = :categoryId 
            ORDER BY name, description LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
        categoryId: Long?, currency: String, offset: Int, pageSize: Int
    ): List<ProductWithMinMaxPrices>

    data class ProductWithMinMaxPrices(
        @Embedded val productRoomEntity: ProductRoomEntity,
        val minPrice: Double?,
        val maxPrice: Double?,
        val lastUpdatedTimestamp: Long?,
    )
}
