package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.*
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalProductDataSource
import com.igrocery.overpriced.domain.productpricehistory.models.Product
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.ProductRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper.ProductMapper
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private val log = Logger { }

@Singleton
class ProductRepository @Inject internal constructor(
    @LocalDataSource private val localProductDataSource: ILocalProductDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IProductRepository {

    private val productMapper = ProductMapper()

    override suspend fun insert(item: Product): Long {
        return transaction.execute {
            item.creationTimestamp = System.currentTimeMillis()
            item.updateTimestamp = item.creationTimestamp

            val id = localProductDataSource.insert(productMapper.mapToData(item))
            item.id = id
            id
        }
    }

    override suspend fun update(item: Product) {
        transaction.execute {
            item.updateTimestamp = System.currentTimeMillis()
            localProductDataSource.update(productMapper.mapToData(item))
        }
    }

    override suspend fun delete(item: Product) {
        transaction.execute {
            localProductDataSource.delete(productMapper.mapToData(item))
        }
    }

    override fun getProductsPagingSource(query: String?): PagingSource<Int, Product> {
        return ProductsPagingSource(localProductDataSource, productMapper, ioDispatcher, query)
    }

    private class ProductsPagingSource(
        private val localProductDataSource: ILocalProductDataSource,
        private val productMapper: ProductMapper,
        private val ioDispatcher: CoroutineDispatcher,
        private val query: String? = null,
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
                    val pageData = if (queryStr == null) {
                        localProductDataSource.getProductsPage(offset, params.loadSize)
                    } else if (queryStr.isBlank()) {
                        emptyList()
                    } else {
                        localProductDataSource.searchProductsPage(queryStr, offset, params.loadSize)
                    }
                    LoadResult.Page(
                        data = pageData.map { productMapper.mapFromData(it) },
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

    override fun getProductByNameAndDescription(
        name: String,
        description: String?
    ): Flow<Product?> {
        return localProductDataSource.getProductByNameAndDescription(name, description)
            .map { it?.let { productMapper.mapFromData(it) } }
    }

    override fun getProductByBarcode(barcode: String): Flow<Product?> {
        return localProductDataSource.getProductByBarcode(barcode)
            .map { it?.let { productMapper.mapFromData(it) } }
    }

    override fun getProductCountWithCategory(category: Category?): Flow<Int> {
        return localProductDataSource.getProductCountWithCategory(category)
    }

}
