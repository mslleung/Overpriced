package com.igrocery.overpriced.infrastructure.preference.datasources

import com.igrocery.overpriced.infrastructure.preference.datasources.datastore.AppPreferenceProto
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface IPreferenceDataSource {

    suspend fun updatePreferredCurrency(currency: Currency)

    fun get(): Flow<AppPreferenceProto>

}
