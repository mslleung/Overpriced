package com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local

import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.daos.CategoryDao
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.entities.CategoryRoomEntity
import kotlinx.coroutines.flow.Flow

internal interface ILocalCategoryDataSource : IBaseLocalDataSource<CategoryId, CategoryRoomEntity> {

    fun getCategory(id: CategoryId): Flow<CategoryRoomEntity>

    fun getAllCategories(): Flow<List<CategoryRoomEntity>>

    fun getAllCategoriesWithProductCount(): Flow<List<CategoryDao.CategoryWithProductCount>>

}
