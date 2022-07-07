package com.igrocery.overpriced.infrastructure.productpricehistory

import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.DefaultDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper.PriceRecordMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRecordRepository @Inject internal constructor(
    @LocalDataSource private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
    private val transaction: Transaction
) : IPriceRecordRepository {

    private val priceRecordMapper = PriceRecordMapper()

    override suspend fun insert(item: PriceRecord): Long {
        return transaction.execute {
            item.creationTimestamp = System.currentTimeMillis()
            item.updateTimestamp = item.creationTimestamp

            val id = localPriceRecordDataSource.insertPriceRecord(priceRecordMapper.mapToData(item))
            item.id = id
            id
        }
    }

    override suspend fun update(item: PriceRecord) {
        transaction.execute {
            item.updateTimestamp = System.currentTimeMillis()
            localPriceRecordDataSource.updatePriceRecord(priceRecordMapper.mapToData(item))
        }
    }

    override suspend fun delete(item: PriceRecord) {
        transaction.execute {
            localPriceRecordDataSource.deletePriceRecord(priceRecordMapper.mapToData(item))
        }
    }

    override fun getPriceRecordsByProductId(productId: Long): Flow<List<PriceRecord>> {
        return localPriceRecordDataSource.getPriceRecordsByProductId(productId)
            .map { priceRecordRoomEntities ->
                priceRecordRoomEntities.map {
                    priceRecordMapper.mapFromData(it)
                }
            }
    }

}
