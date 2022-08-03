package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow

internal interface ILocalProductDataSource {

    fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver)

    suspend fun insert(productRoomEntity: ProductRoomEntity): Long

    suspend fun update(productRoomEntity: ProductRoomEntity)

    suspend fun delete(productRoomEntity: ProductRoomEntity)

    suspend fun getProductsPage(offset: Int, pageSize: Int): List<ProductRoomEntity>

    fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<ProductRoomEntity?>

    fun getProductByBarcode(
        barcode: String,
    ): Flow<ProductRoomEntity?>

    suspend fun searchProductsByNamePaging(query: String, offset: Int, pageSize: Int): List<ProductRoomEntity>

    fun getProductCountByCategoryId(categoryId: Long): Flow<Int>

    fun getProductByCategoryIdPaging(categoryId: Long, offset: Int, pageSize: Int): List<ProductRoomEntity>

}
