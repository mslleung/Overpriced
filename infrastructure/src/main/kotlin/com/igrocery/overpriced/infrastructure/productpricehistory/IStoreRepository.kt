package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IStoreRepository : BaseRepository<StoreId, Store> {

    fun getStoresPaging(): PagingSource<Int, Store>

    fun getStore(id: StoreId): Flow<Store?>

    fun getStoresCount(): Flow<Int>

    fun getStoresWithMinMaxPricesPaging(
        productId: ProductId,
        currency: Currency
    ): PagingSource<Int, StoreWithMinMaxPrices>

}
