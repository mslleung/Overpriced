package com.igrocery.overpriced.application.di

import com.igrocery.overpriced.infrastructure.grocerylist.GroceryListItemRepository
import com.igrocery.overpriced.infrastructure.grocerylist.GroceryListRepository
import com.igrocery.overpriced.infrastructure.grocerylist.IGroceryListItemRepository
import com.igrocery.overpriced.infrastructure.grocerylist.IGroceryListRepository
import com.igrocery.overpriced.infrastructure.preference.IPreferenceRepository
import com.igrocery.overpriced.infrastructure.preference.PreferenceRepository
import com.igrocery.overpriced.infrastructure.productpricehistory.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCategoryRepository(
        categoryRepository: CategoryRepository,
    ): ICategoryRepository

    @Singleton
    @Binds
    abstract fun bindProductRepository(
        productRepository: ProductRepository,
    ): IProductRepository

    @Singleton
    @Binds
    abstract fun bindPriceRecordRepository(
        priceRecordRepository: PriceRecordRepository
    ): IPriceRecordRepository

    @Singleton
    @Binds
    abstract fun bindStoreRepository(
        storeRepository: StoreRepository
    ): IStoreRepository

    @Singleton
    @Binds
    abstract fun bindGroceryListRepository(
        groceryListRepository: GroceryListRepository
    ): IGroceryListRepository

    @Singleton
    @Binds
    abstract fun bindGroceryListItemRepository(
        groceryListItemRepository: GroceryListItemRepository
    ): IGroceryListItemRepository

    @Singleton
    @Binds
    abstract fun bindPreferenceRepository(
        preferenceRepository: PreferenceRepository
    ): IPreferenceRepository
}
