package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.infrastructure.BaseRepository
import kotlinx.coroutines.flow.Flow

interface IGroceryListRepository : BaseRepository<GroceryListId, GroceryList> {


    fun getGroceryListCount(): Flow<Int>
    fun getAllGroceryListsWithItemCountPaging(
        onDataSourcesInvalidated: PagingSource<Int, GroceryListWithItemCount>.() -> Unit
    ): PagingSource<Int, GroceryListWithItemCount>

}
