package com.igrocery.overpriced.application.productpricehistory

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.PriceRecordId
import com.igrocery.overpriced.domain.ProductId
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.Money
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.domain.productpricehistory.models.SaleQuantity
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.preference.IPreferenceRepository
import com.igrocery.overpriced.infrastructure.productpricehistory.IPriceRecordRepository
import kotlinx.coroutines.flow.first
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRecordService @Inject constructor(
    private val preferenceRepository: IPreferenceRepository,
    private val priceRecordRepository: IPriceRecordRepository,
    private val transaction: Transaction
) {

    suspend fun createPriceRecord(
        productId: ProductId,
        priceAmountText: String,
        quantity: SaleQuantity,
        isSale: Boolean,
        storeId: StoreId,
    ): PriceRecordId {
        return transaction.execute {
            val priceAmount = priceAmountText.trim().toDouble()
            val preferredCurrency =
                preferenceRepository.getAppPreference().first().preferredCurrency

            val priceRecord = PriceRecord(
                productId = productId,
                price = Money(priceAmount, preferredCurrency),
                quantity = quantity,
                isSale = isSale,
                storeId = storeId,
            )
            priceRecordRepository.insert(priceRecord)
        }
    }

    fun getPriceRecordsPaging(
        productId: ProductId,
        storeId: StoreId,
        currency: Currency
    ): PagingSource<Int, PriceRecord> {
        return priceRecordRepository.getPriceRecordsPaging(productId, storeId, currency)
    }

}
