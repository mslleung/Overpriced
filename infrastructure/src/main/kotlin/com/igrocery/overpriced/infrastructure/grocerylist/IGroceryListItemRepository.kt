package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.GroceryListItemId
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem
import com.igrocery.overpriced.infrastructure.BaseRepository

interface IGroceryListItemRepository : BaseRepository<GroceryListItemId, GroceryListItem> {

    fun getAllGroceryListItemsPaging(groceryListId: GroceryListId): PagingSource<Int, GroceryListItem>

}
