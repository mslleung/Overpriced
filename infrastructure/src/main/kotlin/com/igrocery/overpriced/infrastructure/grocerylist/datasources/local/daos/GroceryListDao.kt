package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity

@Dao
internal interface GroceryListDao : BaseDao<GroceryListRoomEntity> {

    @Query(
        """
            SELECT grocery_lists.*, Count(grocery_list_items.grocery_list_id) AS itemCount
            FROM grocery_lists LEFT JOIN grocery_list_items 
                ON grocery_lists.id = grocery_list_items.grocery_list_id
            GROUP BY grocery_lists.id
            ORDER BY grocery_lists.update_timestamp
            LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun getGroceryListsWithItemCountPaging(
        offset: Int,
        pageSize: Int
    ): List<GroceryListWithItemCount>

    data class GroceryListWithItemCount(
        @Embedded val groceryListRoomEntity: GroceryListRoomEntity,
        val itemCount: Int
    )

}
