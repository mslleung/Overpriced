package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import com.igrocery.overpriced.domain.GroceryListId
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

    override suspend fun insert(entity: GroceryListRoomEntity): GroceryListId {
        val time = Clock.System.now().toEpochMilliseconds()
        val entityToInsert = entity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.groceryListDao().insert(entityToInsert)
        require(rowId > 0)
        return GroceryListId(rowId)
    }

    override suspend fun update(entity: GroceryListRoomEntity) {
        val entityToUpdate = entity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.groceryListDao().update(entityToUpdate)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(entity: GroceryListRoomEntity) {
        val rowsDeleted = db.groceryListDao().delete(entity)
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
