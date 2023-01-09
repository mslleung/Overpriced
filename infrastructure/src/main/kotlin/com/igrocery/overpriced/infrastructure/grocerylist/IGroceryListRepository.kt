package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.infrastructure.BaseRepository

interface IGroceryListRepository : BaseRepository<GroceryList> {

    fun getAllGroceryListsWithItemCountPaging(): PagingSource<Int, GroceryListWithItemCount>

}
