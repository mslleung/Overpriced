package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.ProductDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalProductDataSource : IBaseLocalDataSource {

    suspend fun insert(productRoomEntity: ProductRoomEntity): Long

    suspend fun update(productRoomEntity: ProductRoomEntity)

    suspend fun delete(productRoomEntity: ProductRoomEntity)

    suspend fun getProductsPage(offset: Int, pageSize: Int): List<ProductRoomEntity>

    fun getProductById(productId: Long): Flow<ProductRoomEntity?>

    fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<ProductRoomEntity?>

    suspend fun searchProductsByNamePaging(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity>

    suspend fun searchProductsByNameWithMinMaxPricesPaging(
        query: String,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<ProductDao.ProductWithMinMaxPrices>

    suspend fun getProductByCategoryIdPaging(
        categoryId: Long?,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity>

    fun getProductsWithMinMaxPricesByProductIdAndCurrency(
        productId: Long,
        currency: Currency
    ): Flow<ProductDao.ProductWithMinMaxPrices?>

    suspend fun getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
        categoryId: Long?,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<ProductDao.ProductWithMinMaxPrices>

}
