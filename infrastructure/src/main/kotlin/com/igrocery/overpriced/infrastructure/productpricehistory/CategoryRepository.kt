package com.igrocery.overpriced.infrastructure.productpricehistory

import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalCategoryDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject internal constructor(
    @LocalDataSource private val localCategoryDataSource: ILocalCategoryDataSource,
    private val transaction: Transaction,
) : ICategoryRepository {

    override suspend fun insert(item: Category): CategoryId {
        return transaction.execute {
            localCategoryDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: Category) {
        transaction.execute {
            localCategoryDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: Category) {
        transaction.execute {
            localCategoryDataSource.delete(item.toData())
        }
    }

    override fun getCategory(id: CategoryId): Flow<Category?> {
        return localCategoryDataSource.getCategory(id)
            .map { it?.toDomain() }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return localCategoryDataSource.getAllCategories()
            .map { it.map { category -> category.toDomain() } }
    }

    override fun getAllCategoriesWithProductCount(): Flow<List<CategoryWithProductCount>> {
        return localCategoryDataSource.getAllCategoriesWithProductCount()
            .map {
                it.map { data ->
                    if (data.categoryRoomEntity != null) {
                        CategoryWithProductCount(
                            data.categoryRoomEntity.toDomain(),
                            data.productCount
                        )
                    } else {
                        CategoryWithProductCount(
                            null,
                            data.productCount
                        )
                    }
                }
            }
    }
}
