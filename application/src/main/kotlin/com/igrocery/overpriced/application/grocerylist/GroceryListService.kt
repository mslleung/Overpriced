package com.igrocery.overpriced.application.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.grocerylist.IGroceryListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListService @Inject constructor(
    private val groceryListRepository: IGroceryListRepository,
    private val transaction: Transaction
) {

    fun getAllGroceryListsWithItemCountPaging(): PagingSource<Int, GroceryListWithItemCount> {
        return groceryListRepository.getAllGroceryListsWithItemCountPaging()
    }

    suspend fun createNewGroceryList(name: String = "New Grocery List"): Long {
        return transaction.execute {
            val newGroceryList = GroceryList(
                name = name,
            )

            groceryListRepository.insert(newGroceryList)
        }
    }

}
