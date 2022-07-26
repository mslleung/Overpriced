package com.igrocery.overpriced.infrastructure.di

import com.igrocery.overpriced.infrastructure.preference.datasources.IPreferenceDataSource
import com.igrocery.overpriced.infrastructure.preference.datasources.datastore.PreferenceDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.*
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalProductDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.IStoreDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.LocalCategoryDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.LocalPriceRecordDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.LocalProductDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.LocalStoreDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LocalDataSource

    @LocalDataSource
    @Binds
    abstract fun bindLocalCategoryDataSource(
        localCategoryDataSource: LocalCategoryDataSource
    ): ILocalCategoryDataSource

    @LocalDataSource
    @Binds
    abstract fun bindLocalProductDataSource(
        localProductDataSource: LocalProductDataSource
    ): ILocalProductDataSource

    @LocalDataSource
    @Binds
    abstract fun bindLocalPriceRecordDataSource(
        localPriceRecordDataSource: LocalPriceRecordDataSource
    ): ILocalPriceRecordDataSource

    @LocalDataSource
    @Binds
    abstract fun bindLocalStoreDataSource(
        localStoreDataSource: LocalStoreDataSource
    ): IStoreDataSource

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DataStore

    @DataStore
    @Binds
    abstract fun bindPreferenceDataSource(
        preferenceDataSource: PreferenceDataSource
    ): IPreferenceDataSource

//    @Qualifier
//    @Retention(AnnotationRetention.BINARY)
//    annotation class RemoteDataSource

}
