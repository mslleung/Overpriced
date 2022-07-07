package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity
import kotlinx.coroutines.flow.Flow

internal interface ILocalCategoryDataSource {

    fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver)

    suspend fun insert(categoryRoomEntity: CategoryRoomEntity): Long

    suspend fun update(categoryRoomEntity: CategoryRoomEntity)

    suspend fun delete(categoryRoomEntity: CategoryRoomEntity)

    fun getCategoryById(id: Long): Flow<CategoryRoomEntity?>

    fun getAllCategories(): Flow<List<CategoryRoomEntity>>

}
