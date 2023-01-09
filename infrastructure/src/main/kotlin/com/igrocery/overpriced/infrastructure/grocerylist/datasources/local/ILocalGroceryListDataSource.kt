package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate

internal interface ILocalGroceryListDataSource {

    fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver)


}
