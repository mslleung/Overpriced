package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IStoreRepository : BaseRepository<Store> {

    fun getStoresPagingSource(): PagingSource<Int, Store>

    fun getStoreById(id: Long): Flow<Store?>

    fun getStoresCount(): Flow<Int>

    fun getStoresWithMinMaxPricesByProductIdAndCurrency(productId: Long, currency: Currency): PagingSource<Int, StoreWithMinMaxPrices>

}
