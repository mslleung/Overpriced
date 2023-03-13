package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
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

    override suspend fun insert(item: Store): StoreId {
        return transaction.execute {
            localStoreDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: Store) {
        transaction.execute {
            localStoreDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: Store) {
        transaction.execute {
            localStoreDataSource.delete(item.toData())
        }
    }

    override fun getStoresPaging(): PagingSource<Int, Store> {
        return createSimplePagingSource(
            ioDispatcher = ioDispatcher,
            pageDataCreator = { offset, loadSize ->
                localStoreDataSource.getStoresPaging(offset, loadSize)
                    .map { it.toDomain() }
            },
            observedDataSources = listOf(localStoreDataSource)
        )
    }

    override fun getStore(id: StoreId): Flow<Store> {
        return localStoreDataSource.getStore(id)
            .map { it.toDomain() }
    }

    override fun getStoresCount(): Flow<Int> {
        return localStoreDataSource.getStoresCount()
    }

    override fun getStoresWithMinMaxPricesPaging(
        productId: ProductId,
        currency: Currency
    ): PagingSource<Int, StoreWithMinMaxPrices> {
        return createSimplePagingSource(
            ioDispatcher = ioDispatcher,
            pageDataCreator = { offset, loadSize ->
                localStoreDataSource.getStoresWithMinMaxPricesPaging(
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
            },
            observedDataSources = listOf(localStoreDataSource, localPriceRecordDataSource),
        )
    }

}
