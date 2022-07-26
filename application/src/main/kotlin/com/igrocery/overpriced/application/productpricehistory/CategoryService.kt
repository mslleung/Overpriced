package com.igrocery.overpriced.application.productpricehistory

import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.domain.productpricehistory.models.Product
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

    suspend fun createCategory(icon: CategoryIcon, name: String): Long {
        return transaction.execute {
            val category = Category(
                icon = icon,
                name = name,
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

    fun getCategoryById(id: Long): Flow<Category?> {
        return categoryRepository.getCategoryById(id)
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryRepository.getAllCategories()
    }
}
