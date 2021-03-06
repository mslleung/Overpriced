package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper

import com.igrocery.overpriced.domain.productpricehistory.models.Money
import com.igrocery.overpriced.domain.productpricehistory.models.PriceRecord
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.PriceRecordRoomEntity
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

internal class PriceRecordMapper {

    fun mapToData(priceRecord: PriceRecord): PriceRecordRoomEntity {
        return PriceRecordRoomEntity(
            id = priceRecord.id,
            productId = priceRecord.productId,
            price = priceRecord.price.amount,
            currency = priceRecord.price.currency.currencyCode,
            storeId = priceRecord.storeId,
            creationTimestamp = priceRecord.creationTimestamp,
            updateTimestamp = priceRecord.updateTimestamp,
        )
    }

    fun mapFromData(priceRecordRoomEntity: PriceRecordRoomEntity): PriceRecord {
        return PriceRecord(
            id = priceRecordRoomEntity.id,
            productId = priceRecordRoomEntity.productId,
            price = Money(
                amount = priceRecordRoomEntity.price,
                currency = Currency.getInstance(priceRecordRoomEntity.currency)
            ),
            storeId = priceRecordRoomEntity.storeId,
            creationTimestamp = priceRecordRoomEntity.creationTimestamp,
            updateTimestamp = priceRecordRoomEntity.updateTimestamp,
        )
    }

}
