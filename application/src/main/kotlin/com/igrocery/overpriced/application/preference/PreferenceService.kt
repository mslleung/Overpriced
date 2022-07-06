package com.igrocery.overpriced.application.preference

import com.igrocery.overpriced.domain.preference.models.AppPreference
import com.igrocery.overpriced.infrastructure.preference.IPreferenceRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceService @Inject constructor(
    private val preferenceRepository: IPreferenceRepository
) {

    suspend fun updatePreferredCurrency(currency: Currency) {
        preferenceRepository.updatePreferredCurrency(currency)
    }

    fun getAppPreference() = preferenceRepository.getAppPreference()

}
