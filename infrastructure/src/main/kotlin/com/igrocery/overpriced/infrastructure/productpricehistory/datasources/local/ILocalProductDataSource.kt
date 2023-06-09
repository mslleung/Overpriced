package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.ProductDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Currency

internal interface ILocalProductDataSource : IBaseLocalDataSource<ProductId, ProductRoomEntity> {

    fun getProductsPaging(): PagingSource<Int, ProductRoomEntity>

    fun getProduct(productId: ProductId): Flow<ProductRoomEntity>

    fun getProduct(
        name: String,
        quantity: String
    ): Flow<ProductRoomEntity?>

    fun searchProductsPaging(query: String): PagingSource<Int, ProductRoomEntity>

    fun searchProductsWithMinMaxPricesPaging(
        query: String,
        currency: Currency,
    ): PagingSource<Int, ProductDao.ProductWithMinMaxPrices>

    fun getProductPaging(
        categoryId: CategoryId?,
    ): PagingSource<Int, ProductRoomEntity>

    fun getProductWithMinMaxPrices(
        productId: ProductId,
        currency: Currency
    ): Flow<ProductDao.ProductWithMinMaxPrices?>

    fun getProductsWithMinMaxPricesPaging(
        categoryId: CategoryId?,
        currency: Currency,
    ): PagingSource<Int, ProductDao.ProductWithMinMaxPrices>

}
