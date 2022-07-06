package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow

interface IStoreRepository : BaseRepository<Store> {

    fun getStoresPagingSource(): PagingSource<Int, Store>

    fun getStoreById(id: Long): Flow<Store?>

    fun getStoresCount(): Flow<Int>

}
