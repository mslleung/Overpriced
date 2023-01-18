package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.InvalidationObserverDelegate
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

    private val invalidationObserverDelegate = InvalidationObserverDelegate(db, "categories")

    override fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver) {
        invalidationObserverDelegate.addWeakInvalidationObserver(invalidationObserver)
    }

    override suspend fun insert(categoryRoomEntity: CategoryRoomEntity): Long {
        val time = Clock.System.now().toEpochMilliseconds()
        val entity = categoryRoomEntity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.categoryDao().insert(entity)
        require(rowId > 0)
        return rowId
    }

    override suspend fun update(categoryRoomEntity: CategoryRoomEntity) {
        val entity = categoryRoomEntity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.categoryDao().update(entity)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(categoryRoomEntity: CategoryRoomEntity) {
        val rowsDeleted = db.categoryDao().delete(categoryRoomEntity)
        require(rowsDeleted == 1)
    }

    override fun getCategoryById(id: Long): Flow<CategoryRoomEntity?> {
        return db.categoryDao().getCategoryById(id)
    }

    override fun getAllCategories(): Flow<List<CategoryRoomEntity>> {
        return db.categoryDao().getAllCategories()
    }

    override fun getAllCategoriesWithProductCount(): Flow<List<CategoryDao.CategoryWithProductCount>> {
        return db.categoryDao().getCategoryWithProductCount()
    }
}
