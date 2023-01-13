package com.igrocery.overpriced.infrastructure

import androidx.paging.PagingState

open class SimplePagingSource<T : Any> : androidx.paging.PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        TODO("Not yet implemented")
    }

}
