package com.igrocery.overpriced.infrastructure

internal interface IBaseLocalDataSource {

    fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver)

}
