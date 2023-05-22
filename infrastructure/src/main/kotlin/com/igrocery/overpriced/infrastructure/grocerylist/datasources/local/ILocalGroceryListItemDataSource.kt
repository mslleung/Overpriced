package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.GroceryListItemId
import com.igrocery.overpriced.infrastructure.IBaseLocalDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListItemRoomEntity

internal interface ILocalGroceryListItemDataSource :
    IBaseLocalDataSource<GroceryListItemId, GroceryListItemRoomEntity> {

    fun getAllGroceryListItemsPaging(groceryListId: GroceryListId): PagingSource<Int, GroceryListItemRoomEntity>

}
