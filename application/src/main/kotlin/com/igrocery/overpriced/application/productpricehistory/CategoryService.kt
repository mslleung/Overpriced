package com.igrocery.overpriced.application.productpricehistory

import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.productpricehistory.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryService @Inject constructor(
    private val categoryRepository: ICategoryRepository,
    private val transaction: Transaction
) {

    suspend fun createCategory(icon: CategoryIcon, name: String): CategoryId {
        return transaction.execute {
            val category = Category(
                icon = icon,
                name = name.trim(),
            )
            categoryRepository.insert(category)
        }
    }

    suspend fun updateCategory(category: Category) {
        transaction.execute {
            categoryRepository.update(category)
        }
    }

    suspend fun deleteCategory(category: Category) {
        transaction.execute {
            categoryRepository.delete(category)
        }
    }

    fun getCategory(id: CategoryId): Flow<Category> {
        return categoryRepository.getCategory(id)
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryRepository.getAllCategories()
    }

    /**
     * Return all categories with their product count. Category can be null to indicate products
     * without category.
     *
     * @return The categories with their respective product count in a map.
     */
    fun getAllCategoriesWithProductCount(): Flow<List<CategoryWithProductCount>> {
        return categoryRepository.getAllCategoriesWithProductCount()
    }
}
