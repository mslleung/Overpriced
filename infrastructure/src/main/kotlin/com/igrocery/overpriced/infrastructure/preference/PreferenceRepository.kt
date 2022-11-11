package com.igrocery.overpriced.infrastructure.preference

import com.igrocery.overpriced.domain.preference.models.AppPreference
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.DataStore
import com.igrocery.overpriced.infrastructure.di.DefaultDispatcher
import com.igrocery.overpriced.infrastructure.preference.datasources.IPreferenceDataSource
import com.igrocery.overpriced.infrastructure.preference.datasources.datastore.mapper.AppPreferenceMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject internal constructor(
    @DataStore private val preferenceDataSource: IPreferenceDataSource,
    private val appPreferenceMapper: AppPreferenceMapper,
    private val externalScope: CoroutineScope,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : IPreferenceRepository {

    override suspend fun updatePreferredCurrency(currency: Currency) {
        externalScope.launch(defaultDispatcher) {
            preferenceDataSource.updatePreferredCurrency(currency)
        }.join()
    }

    override fun getAppPreference(): Flow<AppPreference> {
        return preferenceDataSource.get()
            .flowOn(defaultDispatcher)
            .map { appPreferenceMapper.mapFromData(it) }
    }

}
