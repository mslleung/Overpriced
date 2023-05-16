package com.igrocery.overpriced.application.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.grocerylist.IGroceryListItemRepository
import com.igrocery.overpriced.infrastructure.grocerylist.IGroceryListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListService @Inject constructor(
    private val groceryListRepository: IGroceryListRepository,
    private val groceryListItemRepository: IGroceryListItemRepository,
    private val transaction: Transaction
) {

    suspend fun createNewGroceryList(name: String): GroceryListId {
        return transaction.execute {
            val newGroceryList = GroceryList(
                name = name,
            )

            groceryListRepository.insert(newGroceryList)
        }
    }

    suspend fun editGroceryList(editedGroceryList: GroceryList) {
        return transaction.execute {
            groceryListRepository.update(editedGroceryList)
        }
    }

    suspend fun deleteGroceryList(groceryList: GroceryList) {
        return transaction.execute {
            groceryListRepository.delete(groceryList)
        }
    }

    fun getGroceryList(id: GroceryListId): Flow<GroceryList?> {
        return groceryListRepository.getGroceryList(id)
    }

    fun getGroceryListCount(): Flow<Int> {
        return groceryListRepository.getGroceryListCount()
    }

    fun getAllGroceryListsWithItemCountPaging(
        onDataSourcesInvalidated: PagingSource<Int, GroceryListWithItemCount>.() -> Unit
    ): PagingSource<Int, GroceryListWithItemCount> {
        return groceryListRepository.getAllGroceryListsWithItemCountPaging(onDataSourcesInvalidated)
    }

    fun getAllGroceryListItemsPaging(groceryListId: GroceryListId): PagingSource<Int, GroceryListItem> {
        return groceryListItemRepository.getAllGroceryListItemsPaging(groceryListId)
    }

    suspend fun addItemToGroceryList(
        groceryListId: GroceryListId,
        itemName: String,
        itemDescription: String
    ) {
        return transaction.execute {
            val newItem = GroceryListItem(
                groceryListId = groceryListId,
                name = itemName,
                description = itemDescription
            )

            groceryListItemRepository.insert(newItem)
        }
    }

    suspend fun updateGroceryListItem(item: GroceryListItem) {
        return transaction.execute {
            groceryListItemRepository.update(item)
        }
    }

    suspend fun deleteGroceryListItem(item: GroceryListItem) {
        return transaction.execute {
            groceryListItemRepository.delete(item)
        }
    }

}
