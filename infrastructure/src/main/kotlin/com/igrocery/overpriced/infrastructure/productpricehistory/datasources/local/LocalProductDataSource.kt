package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.productpricehistory.models.ProductQuantity
import com.igrocery.overpriced.infrastructure.AppDatabase
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

    override fun getProductsPaging(): PagingSource<Int, ProductRoomEntity> {
        return db.productDao().getProductsPaging()
    }

    override fun getProduct(productId: ProductId): Flow<ProductRoomEntity> {
        return db.productDao().getProduct(productId.value)
            .distinctUntilChanged()
    }

    override fun getProduct(
        name: String,
        quantity: ProductQuantity
    ): Flow<ProductRoomEntity?> {
        return db.productDao().getProduct(name, quantity.amount, quantity.unit.name)
            .distinctUntilChanged()
    }

    override fun searchProductsPaging(
        query: String,
    ): PagingSource<Int, ProductRoomEntity> {
        return db.productDao().searchProductsPaging(
            query
        )
    }

    override fun searchProductsWithMinMaxPricesPaging(
        query: String,
        currency: Currency
    ): PagingSource<Int, ProductDao.ProductWithMinMaxPrices> {
        return db.productDao().searchProductsWithMinMaxPricesPaging(
            query,
            currency.currencyCode
        )
    }

    override fun getProductPaging(categoryId: CategoryId?): PagingSource<Int, ProductRoomEntity> {
        return db.productDao().getProductPaging(categoryId?.value)
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

    override fun getProductsWithMinMaxPricesPaging(
        categoryId: CategoryId?,
        currency: Currency,
    ): PagingSource<Int, ProductDao.ProductWithMinMaxPrices> {
        return db.productDao().getProductsWithMinMaxPricesPaging(
            categoryId?.value,
            currency.currencyCode,
        )
    }
}
