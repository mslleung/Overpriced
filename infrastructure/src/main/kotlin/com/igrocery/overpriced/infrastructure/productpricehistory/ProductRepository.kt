package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.igrocery.overpriced.domain.productpricehistory.dtos.ProductWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.infrastructure.Transaction
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
        return SearchProductsPagingSource(
            localProductDataSource,
            ioDispatcher,
            query,
        )
    }

    private class SearchProductsPagingSource(
        private val localProductDataSource: ILocalProductDataSource,
        private val ioDispatcher: CoroutineDispatcher,
        private val query: String,
    ) : PagingSource<Int, Product>(), InvalidationObserverDelegate.InvalidationObserver {

        init {
            localProductDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages

                    val queryStr = query
                    val pageData = if (queryStr.isBlank()) {
                        emptyList()
                    } else {
                        localProductDataSource.searchProductsByNamePaging(
                            queryStr,
                            offset,
                            params.loadSize
                        )
                    }
                    LoadResult.Page(
                        data = pageData.map { it.toDomain() },
                        prevKey = if (pageNumber <= 1) null else pageNumber - 1,
                        nextKey = if (pageData.isEmpty()) null else pageNumber + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
            // Try to find the page key of the closest page to anchorPosition, from
            // either the prevKey or the nextKey, but you need to handle nullability
            // here:
            //  * prevKey == null -> anchorPage is the first page.
            //  * nextKey == null -> anchorPage is the last page.
            //  * both prevKey and nextKey null -> anchorPage is the initial page, so
            //    just return null.
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }

    override fun searchProductsByNameWithMinMaxPricesPaging(
        query: String,
        currency: Currency
    ): PagingSource<Int, ProductWithMinMaxPrices> {
        return SearchProductsWithMinMaxPricesPagingSource(
            localProductDataSource,
            localPriceRecordDataSource,
            ioDispatcher,
            query,
            currency
        )
    }

    private class SearchProductsWithMinMaxPricesPagingSource(
        private val localProductDataSource: ILocalProductDataSource,
        localPriceRecordDataSource: ILocalPriceRecordDataSource,
        private val ioDispatcher: CoroutineDispatcher,
        private val query: String,
        private val currency: Currency
    ) : PagingSource<Int, ProductWithMinMaxPrices>(),
        InvalidationObserverDelegate.InvalidationObserver {

        init {
            localProductDataSource.addInvalidationObserver(this)
            localPriceRecordDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductWithMinMaxPrices> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages

                    val queryStr = query
                    val pageData = if (queryStr.isBlank()) {
                        emptyList()
                    } else {
                        localProductDataSource.searchProductsByNameWithMinMaxPricesPaging(
                            queryStr,
                            currency,
                            offset,
                            params.loadSize
                        )
                    }
                    LoadResult.Page(
                        data = pageData.map {
                            ProductWithMinMaxPrices(
                                it.productRoomEntity.toDomain(),
                                it.minPrice,
                                it.maxPrice,
                                it.lastUpdatedTimestamp
                            )
                        },
                        prevKey = if (pageNumber <= 1) null else pageNumber - 1,
                        nextKey = if (pageData.isEmpty()) null else pageNumber + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, ProductWithMinMaxPrices>): Int? {
            // Try to find the page key of the closest page to anchorPosition, from
            // either the prevKey or the nextKey, but you need to handle nullability
            // here:
            //  * prevKey == null -> anchorPage is the first page.
            //  * nextKey == null -> anchorPage is the last page.
            //  * both prevKey and nextKey null -> anchorPage is the initial page, so
            //    just return null.
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
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
        return ProductsByCategoryPagingSource(
            localProductDataSource,
            ioDispatcher,
            categoryId
        )
    }

    private class ProductsByCategoryPagingSource(
        private val localProductDataSource: ILocalProductDataSource,
        private val ioDispatcher: CoroutineDispatcher,
        private val categoryId: Long?,
    ) : PagingSource<Int, Product>(), InvalidationObserverDelegate.InvalidationObserver {

        init {
            localProductDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages

                    val pageData = localProductDataSource.getProductByCategoryIdPaging(
                        categoryId,
                        offset,
                        params.loadSize
                    )
                    LoadResult.Page(
                        data = pageData.map { it.toDomain() },
                        prevKey = if (pageNumber <= 1) null else pageNumber - 1,
                        nextKey = if (pageData.isEmpty()) null else pageNumber + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
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
        return ProductsWithMinMaxPricesByCategoryIdAndCurrencyPagingSource(
            localProductDataSource,
            localPriceRecordDataSource,
            ioDispatcher,
            categoryId,
            currency
        )
    }

    private class ProductsWithMinMaxPricesByCategoryIdAndCurrencyPagingSource(
        private val localProductDataSource: ILocalProductDataSource,
        localPriceRecordDataSource: ILocalPriceRecordDataSource,
        private val ioDispatcher: CoroutineDispatcher,
        private val categoryId: Long?,
        private val currency: Currency
    ) : PagingSource<Int, ProductWithMinMaxPrices>(),
        InvalidationObserverDelegate.InvalidationObserver {

        init {
            localProductDataSource.addInvalidationObserver(this)
            localPriceRecordDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductWithMinMaxPrices> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages

                    val pageData =
                        localProductDataSource.getProductsWithMinMaxPricesByCategoryIdAndCurrencyPaging(
                            categoryId,
                            currency,
                            offset,
                            params.loadSize
                        )
                    LoadResult.Page(
                        data = pageData.map {
                            ProductWithMinMaxPrices(
                                it.productRoomEntity.toDomain(),
                                it.minPrice,
                                it.maxPrice,
                                it.lastUpdatedTimestamp
                            )
                        },
                        prevKey = if (pageNumber <= 1) null else pageNumber - 1,
                        nextKey = if (pageData.isEmpty()) null else pageNumber + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, ProductWithMinMaxPrices>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }
}
