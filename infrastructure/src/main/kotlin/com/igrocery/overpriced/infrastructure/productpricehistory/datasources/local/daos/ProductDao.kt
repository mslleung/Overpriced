package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.room.*
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ProductDao : BaseDao<ProductRoomEntity> {

    @Query("SELECT * FROM products ORDER BY update_timestamp LIMIT :pageSize OFFSET :offset")
    suspend fun getProductsPaging(offset: Int, pageSize: Int): List<ProductRoomEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProduct(id: Long): Flow<ProductRoomEntity>

    @Query(
        """
            SELECT * FROM products
            WHERE products.name = :name AND products.quantity_amount = :quantity AND products.quantity_unit = :unit
        """
    )
    fun getProduct(
        name: String,
        quantity: Double,
        unit: String,
    ): Flow<ProductRoomEntity?>

    @Query(
        """
            SELECT products.*
            FROM products JOIN products_fts ON products.id = products_fts.rowid
            WHERE products_fts MATCH :query
            ORDER BY name, quantity_amount
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
            SELECT products.*,
                MIN(price_records.price) AS minPrice,
                MAX(price_records.price) AS maxPrice,
                MAX(price_records.update_timestamp) AS lastUpdatedTimestamp
            FROM (
                SELECT products.*
                FROM products JOIN products_fts ON products.id = products_fts.rowid
                WHERE products_fts MATCH :query
                ORDER BY name, quantity_amount
                LIMIT :pageSize OFFSET :offset
            ) products LEFT JOIN price_records ON products.id = price_records.product_id
            WHERE price_records.currency = :currency
            GROUP BY products.id
            ORDER BY name, quantity_amount
        """
    )
    suspend fun searchProductsWithMinMaxPricesPaging(
        query: String,
        currency: String,
        offset: Int,
        pageSize: Int
    ): List<ProductWithMinMaxPrices>

    @Query(
        """
            SELECT * FROM products
            WHERE category_id = :categoryId OR (category_id IS NULL AND :categoryId IS NULL)
            ORDER BY name, quantity_amount LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getProductPaging(
        categoryId: Long?,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity>

    @Query(
        """
            SELECT products.*,
                MIN(price_records.price) AS minPrice,
                MAX(price_records.price) AS maxPrice,
                MAX(price_records.update_timestamp) AS lastUpdatedTimestamp
            FROM products LEFT JOIN price_records ON products.id = price_records.product_id
            WHERE products.id = :productId AND price_records.currency = :currency
            GROUP BY products.id
        """
    )
    fun getProductsWithMinMaxPrices(
        productId: Long,
        currency: String?
    ): Flow<ProductWithMinMaxPrices?>

    @Query(
        """
            SELECT products.*,
                MIN(price_records.price) AS minPrice,
                MAX(price_records.price) AS maxPrice,
                MAX(price_records.update_timestamp) AS lastUpdatedTimestamp
            FROM products LEFT JOIN price_records ON products.id = price_records.product_id
            WHERE products.category_id IS :categoryId AND price_records.currency = :currency
            GROUP BY products.id
            ORDER BY name, quantity_amount LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getProductsWithMinMaxPricesPaging(
        categoryId: Long?, currency: String, offset: Int, pageSize: Int
    ): List<ProductWithMinMaxPrices>

    data class ProductWithMinMaxPrices(
        @Embedded val productRoomEntity: ProductRoomEntity,
        val minPrice: Double?,
        val maxPrice: Double?,
        val lastUpdatedTimestamp: Long?,
    )
}
