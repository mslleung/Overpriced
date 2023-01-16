package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.createSimplePagingSource
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRecordRepository @Inject internal constructor(
    @LocalDataSource private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IPriceRecordRepository {

    override suspend fun insert(item: PriceRecord): Long {
        return transaction.execute {
            localPriceRecordDataSource.insertPriceRecord(item.toData())
        }
    }

    override suspend fun update(item: PriceRecord) {
        transaction.execute {
            localPriceRecordDataSource.updatePriceRecord(item.toData())
        }
    }

    override suspend fun delete(item: PriceRecord) {
        transaction.execute {
            localPriceRecordDataSource.deletePriceRecord(item.toData())
        }
    }

    override fun getPriceRecordsPaging(
        productId: Long,
        storeId: Long,
        currency: Currency
    ): PagingSource<Int, PriceRecord> {
        return createSimplePagingSource(
            localPriceRecordDataSource,
            ioDispatcher
        ) {offset, loadSize ->
            localPriceRecordDataSource.getPriceRecordsPaging(
                productId,
                storeId,
                currency,
                offset,
                loadSize
            ).map {
                it.toDomain()
            }
        }
    }

}
