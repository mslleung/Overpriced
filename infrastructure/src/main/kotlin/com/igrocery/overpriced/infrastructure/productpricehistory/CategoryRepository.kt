package com.igrocery.overpriced.infrastructure.productpricehistory

import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule.LocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.ILocalCategoryDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.mapper.CategoryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject internal constructor(
    @LocalDataSource private val localCategoryDataSource: ILocalCategoryDataSource,
    private val transaction: Transaction,
) : ICategoryRepository {

    private val categoryMapper = CategoryMapper()

    override suspend fun insert(item: Category): Long {
        return transaction.execute {
            item.creationTimestamp = System.currentTimeMillis()
            item.updateTimestamp = item.creationTimestamp

            val id = localCategoryDataSource.insert(categoryMapper.mapToData(item))
            item.id = id
            id
        }
    }

    override suspend fun update(item: Category) {
        transaction.execute {
            localCategoryDataSource.update(categoryMapper.mapToData(item))
        }
    }

    override suspend fun delete(item: Category) {
        transaction.execute {
            localCategoryDataSource.delete(categoryMapper.mapToData(item))
        }
    }

    override fun getCategoryById(id: Long): Flow<Category?> {
        return localCategoryDataSource.getCategoryById(id)
            .map { it?.let { categoryMapper.mapFromData(it) } }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return localCategoryDataSource.getAllCategories()
            .map { it.map { category -> categoryMapper.mapFromData(category) } }
    }
}
