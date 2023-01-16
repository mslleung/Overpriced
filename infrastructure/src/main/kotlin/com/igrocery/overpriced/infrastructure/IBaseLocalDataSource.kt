package com.igrocery.overpriced.infrastructure

import com.igrocery.overpriced.infrastructure.productpricehistory.datasources.local.InvalidationObserverDelegate

internal interface IBaseLocalDataSource {

    fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver)

}
