package com.igrocery.overpriced.infrastructure.preference.datasources.datastore.mapper

import com.igrocery.overpriced.domain.preference.models.AppPreference
import com.igrocery.overpriced.infrastructure.preference.datasources.datastore.AppPreferenceProto
import java.util.*
import javax.inject.Inject

internal class AppPreferenceMapper @Inject constructor() {

    fun mapToData(appPreference: AppPreference): AppPreferenceProto {
        return AppPreferenceProto.newBuilder()
            .setPreferredCurrency(appPreference.preferredCurrency.currencyCode)
            .build()
    }

    fun mapFromData(appPreferenceProto: AppPreferenceProto): AppPreference {
        val defaultAppPreference = AppPreference()
        return AppPreference(
            preferredCurrency = if (appPreferenceProto.preferredCurrency.isBlank()) {
                defaultAppPreference.preferredCurrency
            } else {
                Currency.getInstance(appPreferenceProto.preferredCurrency)
            }
        )
    }

}
