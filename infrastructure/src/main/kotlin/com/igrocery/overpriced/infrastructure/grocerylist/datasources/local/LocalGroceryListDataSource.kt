package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity
import com.igrocery.overpriced.infrastructure.InvalidationObserverDelegate
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalGroceryListDataSource @Inject internal constructor(
    private val db: AppDatabase,
) : ILocalGroceryListDataSource {

    private val invalidationObserverDelegate = InvalidationObserverDelegate(db, "price_records")

    override fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver) {
        invalidationObserverDelegate.addWeakInvalidationObserver(invalidationObserver)
    }

    override suspend fun insert(groceryListRoomEntity: GroceryListRoomEntity): Long {
        val time = Clock.System.now().toEpochMilliseconds()
        val entity = groceryListRoomEntity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.groceryListDao().insert(entity)
        require(rowId > 0)
        return rowId
    }

    override suspend fun update(groceryListRoomEntity: GroceryListRoomEntity) {
        val entity = groceryListRoomEntity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.groceryListDao().update(entity)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(groceryListRoomEntity: GroceryListRoomEntity) {
        val rowsDeleted = db.groceryListDao().delete(groceryListRoomEntity)
        require(rowsDeleted == 1)
    }

    override suspend fun getAllGroceryListsWithItemCountPaging(
        offset: Int,
        pageSize: Int
    ): List<GroceryListDao.GroceryListWithItemCount> {
        return db.groceryListDao().getGroceryListsWithItemCountPaging(
            offset,
            pageSize
        )
    }

}
