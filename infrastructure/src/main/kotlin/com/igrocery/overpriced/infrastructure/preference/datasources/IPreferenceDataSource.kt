package com.igrocery.overpriced.infrastructure.preference.datasources

import com.igrocery.overpriced.domain.preference.models.AppPreference
import com.igrocery.overpriced.infrastructure.preference.datasources.datastore.AppPreferenceProto
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IPreferenceDataSource {

    suspend fun updatePreferredCurrency(currency: String)

    fun get(): Flow<AppPreferenceProto>

}
