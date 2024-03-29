package com.igrocery.overpriced.infrastructure.preference.datasources.datastore

import android.content.Context
import com.igrocery.overpriced.infrastructure.preference.datasources.IPreferenceDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PreferenceDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : IPreferenceDataSource {

    override suspend fun updatePreferredCurrency(currency: Currency) {
        context.preferenceDataStore.updateData {
            it.toBuilder()
                .setPreferredCurrency(currency.currencyCode)
                .build()
        }
    }

    override fun get(): Flow<AppPreferenceProto> {
        return context.preferenceDataStore.data.distinctUntilChanged()
    }

}
