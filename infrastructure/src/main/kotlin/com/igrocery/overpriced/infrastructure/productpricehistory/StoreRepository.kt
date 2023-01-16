package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.createSimplePagingSource
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalStoreDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject internal constructor(
    @LocalDataSource private val localStoreDataSource: ILocalStoreDataSource,
    @LocalDataSource private val localPriceRecordDataSource: ILocalPriceRecordDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IStoreRepository {

    override suspend fun insert(item: Store): Long {
        return transaction.execute {
            localStoreDataSource.insertStore(item.toData())
        }
    }

    override suspend fun update(item: Store) {
        transaction.execute {
            localStoreDataSource.updateStore(item.toData())
        }
    }

    override suspend fun delete(item: Store) {
        transaction.execute {
            localStoreDataSource.deleteStore(item.toData())
        }
    }

    override fun getStoresPagingSource(): PagingSource<Int, Store> {
        return createSimplePagingSource(
            localStoreDataSource,
            ioDispatcher
        ) { offset, loadSize ->
            localStoreDataSource.getStoresPaging(offset, loadSize)
                .map { it.toDomain() }
        }
    }

    override fun getStoreById(id: Long): Flow<Store?> {
        return localStoreDataSource.getStoreById(id)
            .map { it?.toDomain() }
    }

    override fun getStoresCount(): Flow<Int> {
        return localStoreDataSource.getStoresCount()
    }

    override fun getStoresWithMinMaxPricesByProductIdAndCurrency(
        productId: Long,
        currency: Currency
    ): PagingSource<Int, StoreWithMinMaxPrices> {
        return createSimplePagingSource(
            listOf(
                localStoreDataSource,
                localPriceRecordDataSource,
            ),
            ioDispatcher,
        ) { offset, loadSize ->
            localStoreDataSource.getStoresWithMinMaxPricesByProductIdAndCurrencyPaging(
                productId,
                currency,
                offset,
                loadSize
            ).map {
                StoreWithMinMaxPrices(
                    it.storeRoomEntity.toDomain(),
                    it.minPrice,
                    it.maxPrice,
                    it.lastUpdatedTimestamp
                )
            }
        }
    }

}
