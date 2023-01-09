package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos

import androidx.room.Dao
import com.igrocery.overpriced.infrastructure.BaseDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListItemRoomEntity

@Dao
internal interface GroceryListItemDao : BaseDao<GroceryListItemRoomEntity> {



}
