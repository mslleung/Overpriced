package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListItemRoomEntity

@Dao
internal interface GroceryListItemDao : BaseDao<GroceryListItemRoomEntity> {

    @Query(
        """
            SELECT *
            FROM grocery_list_items
            WHERE grocery_list_id = :groceryListId
            ORDER BY name, update_timestamp
        """
    )
    fun getAllGroceryListItemsPaging(groceryListId: Long): PagingSource<Int, GroceryListItemRoomEntity>

}
