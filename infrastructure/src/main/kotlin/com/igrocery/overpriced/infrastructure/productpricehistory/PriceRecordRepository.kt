package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRecordRepository @Inject internal constructor(
    @LocalDataSource private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IPriceRecordRepository {

    override suspend fun insert(item: PriceRecord): Long {
        return transaction.execute {
            localPriceRecordDataSource.insertPriceRecord(item.toData())
        }
    }

    override suspend fun update(item: PriceRecord) {
        transaction.execute {
            localPriceRecordDataSource.updatePriceRecord(item.toData())
        }
    }

    override suspend fun delete(item: PriceRecord) {
        transaction.execute {
            localPriceRecordDataSource.deletePriceRecord(item.toData())
        }
    }

    override fun getPriceRecordsPaging(
        productId: Long,
        storeId: Long,
        currency: Currency
    ): PagingSource<Int, PriceRecord> {
        return PriceRecordsPagingSource(
            localPriceRecordDataSource,
            ioDispatcher,
            productId,
            storeId,
            currency
        )
    }

    private class PriceRecordsPagingSource(
        private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
        private val ioDispatcher: CoroutineDispatcher,
        private val productId: Long,
        private val storeId: Long,
        private val currency: Currency,
    ) : PagingSource<Int, PriceRecord>(), InvalidationObserverDelegate.InvalidationObserver {

        init {
            localPriceRecordDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PriceRecord> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages
                    val pageData = localPriceRecordDataSource.getPriceRecordsPaging(
                        productId,
                        storeId,
                        currency,
                        offset,
                        params.loadSize
                    )
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

        override fun getRefreshKey(state: PagingState<Int, PriceRecord>): Int? {
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
