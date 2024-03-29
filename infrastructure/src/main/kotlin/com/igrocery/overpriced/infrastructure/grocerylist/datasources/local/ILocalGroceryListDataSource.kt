package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity
import kotlinx.coroutines.flow.Flow

internal interface ILocalGroceryListDataSource :
    IBaseLocalDataSource<GroceryListId, GroceryListRoomEntity> {

    fun getGroceryList(id: GroceryListId): Flow<GroceryListRoomEntity?>

    fun getGroceryListCount(): Flow<Int>

    fun getAllGroceryListsWithItemCountPaging(): PagingSource<Int, GroceryListDao.GroceryListWithItemCount>

}
