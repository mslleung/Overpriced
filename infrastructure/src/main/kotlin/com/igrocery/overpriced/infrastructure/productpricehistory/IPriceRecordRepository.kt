package com.igrocery.overpriced.infrastructure.productpricehistory

import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow

interface IPriceRecordRepository : BaseRepository<PriceRecord> {

    fun getPriceRecordsByProductId(productId: Long): Flow<List<PriceRecord>>

}
