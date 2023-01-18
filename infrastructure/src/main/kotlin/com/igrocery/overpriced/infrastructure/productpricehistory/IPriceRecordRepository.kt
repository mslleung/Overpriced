package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.PriceRecordId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.BaseRepository
import java.util.Currency

interface IPriceRecordRepository : BaseRepository<PriceRecordId, PriceRecord> {

    fun getPriceRecordsPaging(
        productId: ProductId,
        storeId: StoreId,
        currency: Currency
    ): PagingSource<Int, PriceRecord>

}
