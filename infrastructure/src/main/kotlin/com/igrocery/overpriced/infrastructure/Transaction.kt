package com.igrocery.overpriced.infrastructure

import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages database transactions. It exposes the concept of transactional consistency to the application layer.
 */
@Singleton
class Transaction @Inject internal constructor(
    private val db: AppDatabase,
) {
    suspend fun <T> execute(actions: suspend () -> T): T {
        return db.withTransaction {    // it is ok to nest transactions
            actions()
        }
    }
}
