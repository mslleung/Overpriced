package com.igrocery.overpriced.infrastructure.productpricehistory

import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository : BaseRepository<CategoryId, Category> {

    fun getCategory(id: CategoryId): Flow<Category>

    fun getAllCategories(): Flow<List<Category>>

    fun getAllCategoriesWithProductCount(): Flow<List<CategoryWithProductCount>>

}
