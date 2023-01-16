package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity

internal interface ILocalGroceryListDataSource : IBaseLocalDataSource {

    suspend fun insert(groceryListRoomEntity: GroceryListRoomEntity): Long

    suspend fun update(groceryListRoomEntity: GroceryListRoomEntity)

    suspend fun delete(groceryListRoomEntity: GroceryListRoomEntity)

    suspend fun getAllGroceryListsWithItemCountPaging(
        offset: Int,
        pageSize: Int
    ): List<GroceryListDao.GroceryListWithItemCount>

}
