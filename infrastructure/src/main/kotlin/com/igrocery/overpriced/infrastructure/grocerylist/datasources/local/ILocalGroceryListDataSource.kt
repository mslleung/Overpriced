package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate

internal interface ILocalGroceryListDataSource {

    fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver)

    suspend fun insert(groceryListRoomEntity: GroceryListRoomEntity): Long

    suspend fun update(groceryListRoomEntity: GroceryListRoomEntity)

    suspend fun delete(groceryListRoomEntity: GroceryListRoomEntity)

    suspend fun getAllGroceryListsWithItemCountPaging(
        offset: Int,
        pageSize: Int
    ): List<GroceryListDao.GroceryListWithItemCount>

}
