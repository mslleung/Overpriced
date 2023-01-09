package com.igrocery.overpriced.infrastructure.grocerylist

import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.infrastructure.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListRepository @Inject internal constructor(

    private val transaction: Transaction,
) : IGroceryListRepository {

    override suspend fun insert(item: Category): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: Category) {
        TODO("Not yet implemented")
    }
}
