package com.igrocery.overpriced.infrastructure

import androidx.paging.PagingSource
import androidx.paging.PagingState

internal class MappedPagingSource<DomainT : Any, DataT : Any>(
    private val dataPagingSource: PagingSource<Int, DataT>,
    private val mapper: (DataT) -> DomainT
) : PagingSource<Int, DomainT>() {

    init {
        dataPagingSource.registerInvalidatedCallback {
            invalidate()
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DomainT> {
        return when (val dataResult = dataPagingSource.load(params)) {
            is LoadResult.Error -> LoadResult.Error(dataResult.throwable)
            is LoadResult.Invalid -> LoadResult.Invalid()
            is LoadResult.Page -> LoadResult.Page(
                data = dataResult.data.map(mapper),
                prevKey = dataResult.prevKey,
                nextKey = dataResult.nextKey,
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DomainT>): Int? {
        return dataPagingSource.getRefreshKey(
            PagingState(
                pages = emptyList(),
                leadingPlaceholderCount = 0,
                anchorPosition = state.anchorPosition,
                config = state.config,
            )
        )
    }

    override val jumpingSupported = dataPagingSource.jumpingSupported

    override val keyReuseSupported = dataPagingSource.keyReuseSupported
}
