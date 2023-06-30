package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.CategoryDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalCategoryDataSource @Inject internal constructor(
    private val db: AppDatabase,
): ILocalCategoryDataSource {

    override suspend fun insert(entity: CategoryRoomEntity): CategoryId {
        val time = Clock.System.now().toEpochMilliseconds()
        val entityToInsert = entity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.categoryDao().insert(entityToInsert)
        require(rowId > 0)
        return CategoryId(rowId)
    }

    override suspend fun update(entity: CategoryRoomEntity) {
        val entityToUpdate = entity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.categoryDao().update(entityToUpdate)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(entity: CategoryRoomEntity) {
        val rowsDeleted = db.categoryDao().delete(entity)
        require(rowsDeleted == 1)
    }

    override fun getCategory(id: CategoryId): Flow<CategoryRoomEntity> {
        return db.categoryDao().getCategory(id.value)
    }

    override fun getAllCategories(): Flow<List<CategoryRoomEntity>> {
        return db.categoryDao().getAllCategories()
    }

    override fun getAllCategoriesWithProductCount(): Flow<List<CategoryDao.CategoryWithProductCount>> {
        return db.categoryDao().getCategoryWithProductCount()
    }
}
