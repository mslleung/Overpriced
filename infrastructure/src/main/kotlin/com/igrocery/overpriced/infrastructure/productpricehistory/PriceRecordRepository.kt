package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.PriceRecordId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.MappedPagingSource
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRecordRepository @Inject internal constructor(
    @LocalDataSource private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
    private val transaction: Transaction,
) : IPriceRecordRepository {

    override suspend fun insert(item: PriceRecord): PriceRecordId {
        return transaction.execute {
            localPriceRecordDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: PriceRecord) {
        transaction.execute {
            localPriceRecordDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: PriceRecord) {
        transaction.execute {
            localPriceRecordDataSource.delete(item.toData())
        }
    }

    override fun getPriceRecordsPaging(
        productId: ProductId,
        storeId: StoreId,
        currency: Currency
    ): PagingSource<Int, PriceRecord> {
        return MappedPagingSource(
            dataPagingSource = localPriceRecordDataSource.getPriceRecordsPaging(productId, storeId, currency),
            mapper = { it.toDomain() }
        )
    }

}
