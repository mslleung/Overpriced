package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.ProductDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Clock
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

    override suspend fun insert(entity: ProductRoomEntity): ProductId {
        val time = Clock.System.now().toEpochMilliseconds()
        val entityToInsert = entity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.productDao().insert(entityToInsert)
        require(rowId > 0)
        return ProductId(rowId)
    }

    override suspend fun update(entity: ProductRoomEntity) {
        val entityToUpdate = entity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.productDao().update(entityToUpdate)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(entity: ProductRoomEntity) {
        val rowsDeleted = db.productDao().delete(entity)
        require(rowsDeleted == 1)
    }

    override suspend fun getProductsPaging(offset: Int, pageSize: Int): List<ProductRoomEntity> {
        return db.productDao().getProductsPaging(offset, pageSize)
    }

    override fun getProduct(productId: ProductId): Flow<ProductRoomEntity> {
        return db.productDao().getProduct(productId.value)
            .distinctUntilChanged()
    }

    override fun getProduct(
        name: String,
        description: String?
    ): Flow<ProductRoomEntity?> {
        return db.productDao().getProduct(name, description)
            .distinctUntilChanged()
    }

    override suspend fun searchProductsPaging(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity> {
        return db.productDao().searchProductsPaging(
            query,
            offset,
            pageSize
        )
    }

    override suspend fun searchProductsWithMinMaxPricesPaging(
        query: String,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<ProductDao.ProductWithMinMaxPrices> {
        return db.productDao().searchProductsWithMinMaxPricesPaging(
            query,
            currency.currencyCode,
            offset,
            pageSize
        )
    }

    override suspend fun getProductPaging(
        categoryId: CategoryId?,
        offset: Int,
        pageSize: Int
    ): List<ProductRoomEntity> {
        return db.productDao().getProductPaging(categoryId?.value, offset, pageSize)
    }

    override fun getProductWithMinMaxPrices(
        productId: ProductId,
        currency: Currency
    ): Flow<ProductDao.ProductWithMinMaxPrices?> {
        return db.productDao().getProductsWithMinMaxPrices(
            productId.value,
            currency.currencyCode,
        )
    }

    override suspend fun getProductsWithMinMaxPricesPaging(
        categoryId: CategoryId?,
        currency: Currency,
        offset: Int,
        pageSize: Int
    ): List<ProductDao.ProductWithMinMaxPrices> {
        return db.productDao().getProductsWithMinMaxPricesPaging(
            categoryId?.value,
            currency.currencyCode,
            offset,
            pageSize
        )
    }
}
