package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.ProductDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Currency
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
        val time = System.nanoTime()
        val entity = productRoomEntity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.productDao().insert(entity)
        require(rowId > 0)
        return rowId
    }

    override suspend fun update(productRoomEntity: ProductRoomEntity) {
        val entity = productRoomEntity.copy(
            updateTimestamp = System.nanoTime()
        )

        val rowsUpdated = db.productDao().update(entity)
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

    override suspend fun searchProductsByNamePaging(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity> {
        return db.productDao().searchProductsPaging(query, offset, pageSize)
    }

    override suspend fun getProductByCategoryIdPaging(
        categoryId: Long?,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity> {
        return db.productDao().getProductByCategoryPaging(categoryId, offset, pageSize)
    }

    override suspend fun getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
        categoryId: Long?,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<ProductDao.ProductWithMinMaxPrices> {
        return db.productDao().getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
            categoryId,
            currency.currencyCode,
            offset,
            pageSize
        )
    }
}
