package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private val log = Logger { }

@Singleton
internal class LocalProductDataSource @Inject internal constructor(
    private val db: AppDatabase,
) : ILocalProductDataSource {

    private val invalidationObserverDelegate = InvalidationObserverDelegate(db, "products")

    override fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver) {
        invalidationObserverDelegate.addWeakInvalidationObserver(invalidationObserver)
    }

    override suspend fun insert(productRoomEntity: ProductRoomEntity): Long {
        val rowId = db.productDao().insert(productRoomEntity)
        require(rowId > 0)
        return rowId
    }

    override suspend fun update(productRoomEntity: ProductRoomEntity) {
        val rowsUpdated = db.productDao().update(productRoomEntity)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(productRoomEntity: ProductRoomEntity) {
        val rowsDeleted = db.productDao().delete(productRoomEntity)
        require(rowsDeleted == 1)
    }

    override suspend fun getProductsPage(offset: Int, pageSize: Int): List<ProductRoomEntity> {
        return db.productDao().getProductsPage(offset, pageSize)
    }

    override fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<ProductRoomEntity?> {
        return db.productDao().getProductByNameAndDescription(name, description)
            .distinctUntilChanged()
    }

    override fun getProductByBarcode(barcode: String): Flow<ProductRoomEntity?> {
        return db.productDao().getProductByBarcode(barcode)
            .distinctUntilChanged()
    }

    override suspend fun searchProductsPage(query: String, offset: Int, pageSize: Int): List<ProductRoomEntity> {
        log.debug("searchProductsPage(query = $query, offset = $offset, pageSize = $pageSize)")
        return db.productDao().searchProducts(query, offset, pageSize)
    }

}
