package com.igrocery.overpriced.infrastructure.preference

import com.igrocery.overpriced.domain.preference.models.AppPreference
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * A repository for application preferences/configs.
 *
 * This repository is exceptional, it does not manage an aggregate root. Please forget about DDD
 * here.
 */
interface IPreferenceRepository {

    suspend fun updatePreferredCurrency(currency: Currency)

    fun getAppPreference(): Flow<AppPreference>

}
