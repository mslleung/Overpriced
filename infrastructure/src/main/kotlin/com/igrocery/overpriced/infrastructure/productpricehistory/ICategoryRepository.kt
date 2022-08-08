package com.igrocery.overpriced.infrastructure.productpricehistory

import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository: BaseRepository<Category> {

    fun getCategoryById(id: Long): Flow<Category?>

    fun getAllCategories(): Flow<List<Category>>

    fun getAllCategoriesWithProductCount(): Flow<Map<Category?, Int>>

}
