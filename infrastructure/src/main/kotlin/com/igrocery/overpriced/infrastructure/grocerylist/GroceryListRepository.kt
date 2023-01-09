package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.ILocalGroceryListDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toDomain
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListRepository @Inject internal constructor(
    @DataSourceModule.LocalDataSource private val localGroceryListDataSource: ILocalGroceryListDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IGroceryListRepository {

    override suspend fun insert(item: GroceryList): Long {
        return transaction.execute {
            localGroceryListDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: GroceryList) {
        transaction.execute {
            localGroceryListDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: GroceryList) {
        transaction.execute {
            localGroceryListDataSource.delete(item.toData())
        }
    }

    override fun getAllGroceryListsWithItemCountPaging(): PagingSource<Int, GroceryListWithItemCount> {
        return GroceryListsWithItemCountPagingSource(localGroceryListDataSource, ioDispatcher)
    }

    private class GroceryListsWithItemCountPagingSource(
        private val localGroceryListDataSource: ILocalGroceryListDataSource,
        private val ioDispatcher: CoroutineDispatcher,
    ) : PagingSource<Int, GroceryListWithItemCount>(),
        InvalidationObserverDelegate.InvalidationObserver {

        init {
            localGroceryListDataSource.addInvalidationObserver(this)
        }

        override fun onInvalidate() {
            // invalidates this datasource when the underlying tables change
            invalidate()
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GroceryListWithItemCount> {
            return withContext(ioDispatcher) {
                try {
                    val pageNumber = params.key ?: 1
                    val offset = (pageNumber - 1) * params.loadSize // all the previous pages

                    val pageData = localGroceryListDataSource.getAllGroceryListsWithItemCountPaging(
                        offset,
                        params.loadSize
                    )
                    LoadResult.Page(
                        data = pageData.map {
                            GroceryListWithItemCount(
                                it.groceryListRoomEntity.toDomain(),
                                it.itemCount
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

        override fun getRefreshKey(state: PagingState<Int, GroceryListWithItemCount>): Int? {
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
