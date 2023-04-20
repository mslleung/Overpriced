package com.igrocery.overpriced.infrastructure

import androidx.room.InvalidationTracker
import com.igrocery.overpriced.shared.Logger
import java.lang.ref.WeakReference

@Suppress("unused")
private val log = Logger { }

internal class InvalidationObserverDelegate(
    db: AppDatabase,
    firstTable: String,
    vararg remainingTables: String
) {

    interface InvalidationObserver {
        fun onInvalidate()
    }

    private val weakInvalidationObservers = mutableListOf<WeakReference<InvalidationObserver>>()

    private val invalidationObserver = object : InvalidationTracker.Observer(firstTable, *remainingTables) {

        override fun onInvalidated(tables: Set<String>) {
            log.error("Tables invalidated: $tables")
            weakInvalidationObservers.let { observers ->    // use let{} to prevent concurrent modifications
                observers.forEach {
                    it.get()?.onInvalidate()
                }
            }
        }
    }

    init {
        db.invalidationTracker.addObserver(invalidationObserver)
    }

    fun addWeakInvalidationObserver(invalidationObserver: InvalidationObserver) {
        weakInvalidationObservers.add(WeakReference(invalidationObserver))
    }

}
