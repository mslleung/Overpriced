package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ProductDao : BaseDao<ProductRoomEntity> {

    @Query("SELECT * FROM products ORDER BY update_timestamp")
    fun getProductsPaging(): PagingSource<Int, ProductRoomEntity>

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
        """
    )
    fun searchProductsPaging(query: String): PagingSource<Int, ProductRoomEntity>

    @Query(
        """
            SELECT products.*,
                MIN(price_records.price / price_records.quantity) AS minPrice,
                MAX(price_records.price / price_records.quantity) AS maxPrice,
                MAX(price_records.update_timestamp) AS lastUpdatedTimestamp
            FROM (
                SELECT products.*
                FROM products JOIN products_fts ON products.id = products_fts.rowid
                WHERE products_fts MATCH :query
                ORDER BY name, quantity_amount
            ) products LEFT JOIN price_records ON products.id = price_records.product_id
            WHERE price_records.currency = :currency
            GROUP BY products.id
            ORDER BY name, quantity_amount
        """
    )
    fun searchProductsWithMinMaxPricesPaging(
        query: String,
        currency: String,
    ): PagingSource<Int, ProductWithMinMaxPrices>

    @Query(
        """
            SELECT * FROM products
            WHERE category_id = :categoryId OR (category_id IS NULL AND :categoryId IS NULL)
            ORDER BY name, quantity_amount
        """
    )
    fun getProductPaging(categoryId: Long?): PagingSource<Int, ProductRoomEntity>

    @Query(
        """
            SELECT products.*,
                MIN(price_records.price / price_records.quantity) AS minPrice,
                MAX(price_records.price / price_records.quantity) AS maxPrice,
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
                MIN(price_records.price / price_records.quantity) AS minPrice,
                MAX(price_records.price / price_records.quantity) AS maxPrice,
                MAX(price_records.update_timestamp) AS lastUpdatedTimestamp
            FROM products LEFT JOIN price_records ON products.id = price_records.product_id
            WHERE products.category_id IS :categoryId AND price_records.currency = :currency
            GROUP BY products.id
            ORDER BY name, quantity_amount 
        """
    )
    fun getProductsWithMinMaxPricesPaging(
        categoryId: Long?, currency: String
    ): PagingSource<Int, ProductWithMinMaxPrices>

    data class ProductWithMinMaxPrices(
        @Embedded val productRoomEntity: ProductRoomEntity,
        val minPrice: Double?,
        val maxPrice: Double?,
        val lastUpdatedTimestamp: Long?,
    )
}
