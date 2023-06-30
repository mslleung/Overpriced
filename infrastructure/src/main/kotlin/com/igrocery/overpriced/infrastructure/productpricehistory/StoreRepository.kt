package com.igrocery.overpriced.infrastructure.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.MappedPagingSource
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalStoreDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject internal constructor(
    @LocalDataSource private val localStoreDataSource: ILocalStoreDataSource,
    private val transaction: Transaction,
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
        return MappedPagingSource(
            dataPagingSource = localStoreDataSource.getStoresPaging(),
            mapper = { it.toDomain() }
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
        return MappedPagingSource(
            dataPagingSource = localStoreDataSource.getStoresWithMinMaxPricesPaging(
                productId,
                currency
            ),
            mapper = {
                StoreWithMinMaxPrices(
                    it.storeRoomEntity.toDomain(),
                    it.minPrice,
                    it.maxPrice,
                    it.lastUpdatedTimestamp
                )
            }
        )
    }

}
