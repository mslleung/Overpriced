package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.BaseRepository
import java.util.Currency

interface IPriceRecordRepository : BaseRepository<PriceRecord> {

    fun getPriceRecordsPaging(
        productId: Long,
        storeId: Long,
        currency: Currency
    ): PagingSource<Int, PriceRecord>

}
