package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface GroceryListDao : BaseDao<GroceryListRoomEntity> {

    @Query("SELECT * FROM grocery_lists WHERE id = :id")
    fun getGroceryList(id: Long) : Flow<GroceryListRoomEntity?>

    @Query("SELECT COUNT(id) FROM grocery_lists")
    fun getGroceryListCount(): Flow<Int>

    @Query(
        """
            SELECT grocery_lists.*,
                COUNT(CASE WHEN grocery_list_items.is_checked = 1 THEN 1 ELSE NULL END) AS checkedItemCount,
                COUNT(grocery_list_items.grocery_list_id) AS totalItemCount
            FROM grocery_lists LEFT JOIN grocery_list_items 
                ON grocery_lists.id = grocery_list_items.grocery_list_id
            GROUP BY grocery_lists.id
            ORDER BY grocery_lists.update_timestamp DESC
        """
    )
    fun getGroceryListsWithItemCountPaging(): PagingSource<Int, GroceryListWithItemCount>

    data class GroceryListWithItemCount(
        @Embedded val groceryListRoomEntity: GroceryListRoomEntity,
        val checkedItemCount: Int,
        val totalItemCount: Int
    )

}
