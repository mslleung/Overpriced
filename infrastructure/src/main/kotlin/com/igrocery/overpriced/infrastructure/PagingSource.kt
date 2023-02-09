package com.igrocery.overpriced.infrastructure

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal data class InvalidationParams<T : Any>(
    val observedDataSources: List<IBaseLocalDataSource<*, *>>, // all data sources involved in the query
    val onDataSourcesInvalidated: PagingSource<Int, T>.() -> Unit = { invalidate() },
)

internal open class SimplePagingSource<T : Any> constructor(
    private val invalidationParams: InvalidationParams<T>,
    private val ioDispatcher: CoroutineDispatcher,
    private val pageDataCreator: suspend (offset: Int, loadSize: Int) -> List<T>,
) : PagingSource<Int, T>(), InvalidationObserverDelegate.InvalidationObserver {

    init {
        dataSources.forEach {
            it.addInvalidationObserver(this)
        }
    }

    override fun onInvalidate() {
        invalidationParams.onDataSourcesInvalidated(this)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return withContext(ioDispatcher) {
            try {
                val pageNumber = params.key ?: 1
                val offset = (pageNumber - 1) * params.loadSize // all the previous pages
                val pageData = pageDataCreator(offset, params.loadSize)
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

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
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

internal fun <T : Any> createSimplePagingSource(
    dataSources: List<IBaseLocalDataSource<*,*>>,    // all data sources involved in the query
    ioDispatcher: CoroutineDispatcher,
    pageDataCreator: suspend (offset: Int, loadSize: Int) -> List<T>,
    onDataSourcesInvalidated: PagingSource<Int, T>.() -> Unit = { invalidate() },
): SimplePagingSource<T> {
    return SimplePagingSource(
        dataSources,
        ioDispatcher,
        pageDataCreator,
        onDataSourcesInvalidated
    )
}

internal fun <T : Any> createSimplePagingSource(
    dataSource: IBaseLocalDataSource<*,*>,    // all data sources involved in the query
    ioDispatcher: CoroutineDispatcher,
    pageDataCreator: suspend (offset: Int, loadSize: Int) -> List<T>,
    onDataSourcesInvalidated: PagingSource<Int, T>.() -> Unit = { invalidate() },
): SimplePagingSource<T> {
    return createSimplePagingSource(
        listOf(dataSource),
        ioDispatcher,
        pageDataCreator,
        onDataSourcesInvalidated
    )
}
