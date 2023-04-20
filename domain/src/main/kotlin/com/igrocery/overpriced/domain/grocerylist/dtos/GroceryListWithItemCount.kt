package com.igrocery.overpriced.domain.grocerylist.dtos

import com.igrocery.overpriced.domain.grocerylist.models.GroceryList

data class GroceryListWithItemCount(
    val groceryList: GroceryList,
    val itemCount: Int
)
