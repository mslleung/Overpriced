package com.igrocery.overpriced.application.grocerylist

import com.igrocery.overpriced.infrastructure.Transaction
import javax.inject.Inject

class GroceryListService @Inject constructor(

    private val transaction: Transaction
) {

}
