package com.igrocery.overpriced.application.productpricehistory

import com.igrocery.overpriced.domain.productpricehistory.models.Money
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.preference.IPreferenceRepository
import com.igrocery.overpriced.infrastructure.productpricehistory.IPriceRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRecordService @Inject constructor(
    private val preferenceRepository: IPreferenceRepository,
    private val priceRecordRepository: IPriceRecordRepository,
    private val transaction: Transaction
) {

    suspend fun createPriceRecord(
        priceAmountText: String,
        productId: Long,
        storeId: Long,
    ): Long {
        return transaction.execute {
            val priceAmount = priceAmountText.trim().toDouble()
            val preferredCurrency = preferenceRepository.getAppPreference().first().preferredCurrency

            val priceRecord = PriceRecord(
                productId = productId,
                price = Money(priceAmount, preferredCurrency),
                storeId = storeId
            )
            priceRecordRepository.insert(priceRecord)
        }
    }

//    suspend fun createPriceRecord(priceRecord: PriceRecord) {
//        priceRecordRepository.insert(priceRecord)
//    }
//
//    suspend fun updatePriceRecord(priceRecord: PriceRecord) {
//        priceRecordRepository.update(priceRecord)
//    }

    fun getPriceRecordsByProductId(productId: Long): Flow<List<PriceRecord>> {
        return priceRecordRepository.getPriceRecordsByProductId(productId)
    }

}
