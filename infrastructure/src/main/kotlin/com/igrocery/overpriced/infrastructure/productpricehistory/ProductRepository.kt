package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.createSimplePagingSource
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalProductDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private val log = Logger { }

@Singleton
class ProductRepository @Inject internal constructor(
    @LocalDataSource private val localProductDataSource: ILocalProductDataSource,
    @LocalDataSource private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IProductRepository {

    override suspend fun insert(item: Product): Long {
        return transaction.execute {
            localProductDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: Product) {
        transaction.execute {
            localProductDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: Product) {
        transaction.execute {
            localProductDataSource.delete(item.toData())
        }
    }

    override fun searchProductsByNamePaging(query: String): PagingSource<Int, Product> {
        return createSimplePagingSource(
            localProductDataSource,
            ioDispatcher
        ) { offset, loadSize ->
            if (query.isBlank()) {
                emptyList()
            } else {
                localProductDataSource.searchProductsByNamePaging(
                    query,
                    offset,
                    loadSize
                ).map { it.toDomain() }
            }
        }
    }

    override fun searchProductsByNameWithMinMaxPricesPaging(
        query: String,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices> {
        return createSimplePagingSource(
            listOf(
                localProductDataSource,
                localPriceRecordDataSource
            ),
            ioDispatcher
        ) { offset, loadSize ->
            if (query.isBlank()) {
                emptyList()
            } else {
                localProductDataSource.searchProductsByNameWithMinMaxPricesPaging(
                    query,
                    currency,
                    offset,
                    loadSize
                ).map {
                    ProductWithMinMaxPrices(
                        it.productRoomEntity.toDomain(),
                        it.minPrice,
                        it.maxPrice,
                        it.lastUpdatedTimestamp
                    )
                }
            }
        }
    }

    override fun getProductById(productId: Long): Flow<Product?> {
        return localProductDataSource.getProductById(productId)
            .map { it?.toDomain() }
            .distinctUntilChanged()
    }

    override fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<Product?> {
        return localProductDataSource.getProductByNameAndDescription(name, description)
            .map { it?.toDomain() }
    }

    override fun getProductsByCategoryIdPaging(categoryId: Long?): PagingSource<Int, Product> {
        return createSimplePagingSource(
            localProductDataSource,
            ioDispatcher
        ) { offset, loadSize ->
            localProductDataSource.getProductByCategoryIdPaging(
                categoryId,
                offset,
                loadSize
            ).map {
                it.toDomain()
            }
        }
    }

    override fun getProductsWithMinMaxPricesByProductIdAndCurrency(
        productId: Long,
        currency: Currency
    ): Flow<ProductWithMinMaxPrices?> {
        return localProductDataSource.getProductsWithMinMaxPricesByProductIdAndCurrency(
            productId,
            currency
        ).map {
            it?.let {
                ProductWithMinMaxPrices(
                    it.productRoomEntity.toDomain(),
                    it.minPrice,
                    it.maxPrice,
                    it.lastUpdatedTimestamp
                )
            }
        }
            .distinctUntilChanged()
    }

    override fun getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
        categoryId: Long?,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices> {
        return createSimplePagingSource(
            listOf(localProductDataSource, localPriceRecordDataSource),
            ioDispatcher
        ) { offset, loadSize ->
            localProductDataSource.getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
                categoryId,
                currency,
                offset,
                loadSize
            ).map {
                ProductWithMinMaxPrices(
                    it.productRoomEntity.toDomain(),
                    it.minPrice,
                    it.maxPrice,
                    it.lastUpdatedTimestamp
                )
            }
        }
    }
}
