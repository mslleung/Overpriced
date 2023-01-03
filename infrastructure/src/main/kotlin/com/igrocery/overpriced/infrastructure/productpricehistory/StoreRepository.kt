package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalStoreDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject internal constructor(
    @LocalDataSource private val localStoreDataSource: ILocalStoreDataSource,
    @LocalDataSource private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IStoreRepository {

    override suspend fun insert(item: Store): Long {
        return transaction.execute {
            localStoreDataSource.insertStore(item.toData())
        }
    }

    override suspend fun update(item: Store) {
        transaction.execute {
            localStoreDataSource.updateStore(item.toData())
        }
    }

    override suspend fun delete(item: Store) {
        transaction.execute {
            localStoreDataSource.deleteStore(item.toData())
        }
    }

    override fun getStoresPagingSource(): PagingSource<Int, Store> {
        return StorePagingSource(localStoreDataSource, ioDispatcher)
    }

    private class StorePagingSource(
        private val localStoreDataSource: ILocalStoreDataSource,
        private val ioDispatcher: CoroutineDispatcher,
    ) : PagingSource<Int, Store>(), InvalidationObserverDelegate.InvalidationObserver {

        init {
            localStoreDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Store> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages
                    val pageData = localStoreDataSource.getStoresPaging(offset, params.loadSize)
                        .map { it.toDomain() }
                    LoadResult.Page(
                        data = pageData,
                        prevKey = if (pageNumber <= 1) null else pageNumber - 1,
                        nextKey = if (pageData.isEmpty()) null else pageNumber + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Store>): Int? {
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

    override fun getStoreById(id: Long): Flow<Store?> {
        return localStoreDataSource.getStoreById(id)
            .map { it?.toDomain() }
    }

    override fun getStoresCount(): Flow<Int> {
        return localStoreDataSource.getStoresCount()
    }

    override fun getStoresWithMinMaxPricesByProductIdAndCurrency(
        productId: Long,
        currency: Currency
    ): PagingSource<Int, StoreWithMinMaxPrices> {
        return StoreWithMinMaxPricesPagingSource(
            localStoreDataSource,
            localPriceRecordDataSource,
            ioDispatcher,
            productId,
            currency
        )
    }

    private class StoreWithMinMaxPricesPagingSource(
        private val localStoreDataSource: ILocalStoreDataSource,
        localPriceRecordDataSource: ILocalPriceRecordDataSource,
        private val ioDispatcher: CoroutineDispatcher,
        private val productId: Long,
        private val currency: Currency,
    ) : PagingSource<Int, StoreWithMinMaxPrices>(),
        InvalidationObserverDelegate.InvalidationObserver {

        init {
            localStoreDataSource.addInvalidationObserver(this)
            localPriceRecordDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreWithMinMaxPrices> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages
                    val pageData =
                        localStoreDataSource.getStoresWithMinMaxPricesByProductIdAndCurrencyPaging(
                            productId,
                            currency,
                            offset,
                            params.loadSize
                        )
                    LoadResult.Page(
                        data = pageData.map {
                            StoreWithMinMaxPrices(
                                it.storeRoomEntity.toDomain(),
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

        override fun getRefreshKey(state: PagingState<Int, StoreWithMinMaxPrices>): Int? {
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

}
