package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.ProductDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalProductDataSource : IBaseLocalDataSource<ProductId, ProductRoomEntity> {

    suspend fun getProductsPaging(offset: Int, pageSize: Int): List<ProductRoomEntity>

    fun getProduct(productId: ProductId): Flow<ProductRoomEntity?>

    fun getProduct(
        name: String,
        description: String?
    ): Flow<ProductRoomEntity?>

    suspend fun searchProductsPaging(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity>

    suspend fun searchProductsWithMinMaxPricesPaging(
        query: String,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<ProductDao.ProductWithMinMaxPrices>

    suspend fun getProductPaging(
        categoryId: CategoryId?,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity>

    fun getProductWithMinMaxPrices(
        productId: ProductId,
        currency: Currency
    ): Flow<ProductDao.ProductWithMinMaxPrices?>

    suspend fun getProductsWithMinMaxPricesPaging(
        categoryId: CategoryId?,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<ProductDao.ProductWithMinMaxPrices>

}
