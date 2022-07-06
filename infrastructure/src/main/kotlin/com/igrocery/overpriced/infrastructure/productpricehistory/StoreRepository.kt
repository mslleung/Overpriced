package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.IStoreDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.LocalStoreDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper.StoreMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject internal constructor(
    @LocalDataSource private val localStoreDataSource: IStoreDataSource,
    private val storeMapper: StoreMapper,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IStoreRepository {

    override suspend fun insert(item: Store): Long {
        return transaction.execute {
            item.creationTimestamp = System.currentTimeMillis()
            item.updateTimestamp = item.creationTimestamp

            val id = localStoreDataSource.insertStore(storeMapper.mapToData(item))
            item.id = id
            id
        }
    }

    override suspend fun update(item: Store) {
        transaction.execute {
            item.updateTimestamp = System.currentTimeMillis()
            localStoreDataSource.updateStore(storeMapper.mapToData(item))
        }
    }

    override suspend fun delete(item: Store) {
        transaction.execute {
            localStoreDataSource.deleteStore(storeMapper.mapToData(item))
        }
    }

    override fun getStoresPagingSource(): PagingSource<Int, Store> {
        return StorePagingSource(localStoreDataSource, storeMapper, ioDispatcher)
    }

    private class StorePagingSource(
        private val localStoreDataSource: IStoreDataSource,
        private val storeMapper: StoreMapper,
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
                    val pageData = localStoreDataSource.getStoresPage(offset, params.loadSize)
                        .map { storeMapper.mapFromData(it) }
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
            .map { it?.let { storeMapper.mapFromData(it) } }
    }

    override fun getStoresCount(): Flow<Int> {
        return localStoreDataSource.getStoresCount()
    }

}
