package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate
import javax.inject.Inject

internal class LocalGroceryListDataSource @Inject internal constructor(
    private val db: AppDatabase,
) : ILocalGroceryListDataSource {

    private val invalidationObserverDelegate = InvalidationObserverDelegate(db, "price_records")

    override fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver) {
        invalidationObserverDelegate.addWeakInvalidationObserver(invalidationObserver)
    }



}
