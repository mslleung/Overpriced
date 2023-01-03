package com.igrocery.overpriced.application.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.productpricehistory.dtos.StoreWithMinMaxPrices
import com.igrocery.overpriced.domain.productpricehistory.models.Address
import com.igrocery.overpriced.domain.productpricehistory.models.GeoCoordinates
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.productpricehistory.IStoreRepository
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.flow.Flow
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private val log = Logger {}

@Singleton
class StoreService @Inject constructor(
    private val storeRepository: IStoreRepository,
    private val transaction: Transaction
) {

    suspend fun createStore(
        storeName: String,
        addressLines: String,
        latitude: Double,
        longitude: Double
    ): Long {
        return transaction.execute {
            val store = Store(
                name = storeName,
                address = Address(
                    lines = addressLines,
                    geoCoordinates = GeoCoordinates(latitude, longitude)
                )
            )

            storeRepository.insert(store)
        }
    }

    suspend fun updateStore(store: Store) {
        transaction.execute {
            storeRepository.update(store)
        }
    }

    suspend fun deleteStore(store: Store) {
        transaction.execute {
            storeRepository.delete(store)
        }
    }

    fun getStoresPagingSource(): PagingSource<Int, Store> {
        return storeRepository.getStoresPagingSource()
    }

    fun getStoreById(id: Long): Flow<Store?> {
        return storeRepository.getStoreById(id)
    }

    fun getStoreCount(): Flow<Int> {
        return storeRepository.getStoresCount()
    }

    fun getStoresWithMinMaxPricesByProductIdAndCurrency(
        productId: Long,
        currency: Currency
    ): PagingSource<Int, StoreWithMinMaxPrices> {
        return storeRepository.getStoresWithMinMaxPricesByProductIdAndCurrency(productId, currency)
    }

}
