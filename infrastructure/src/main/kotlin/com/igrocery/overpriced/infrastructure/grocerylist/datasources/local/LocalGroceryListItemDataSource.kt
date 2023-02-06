package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.GroceryListItemId
import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.InvalidationObserverDelegate
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListItemRoomEntity
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalGroceryListItemDataSource @Inject internal constructor(
    private val db: AppDatabase,
) : ILocalGroceryListItemDataSource {

    private val invalidationObserverDelegate = InvalidationObserverDelegate(db, "grocery_list_items")

    override fun addInvalidationObserver(invalidationObserver: InvalidationObserverDelegate.InvalidationObserver) {
        invalidationObserverDelegate.addWeakInvalidationObserver(invalidationObserver)
    }

    override suspend fun insert(entity: GroceryListItemRoomEntity): GroceryListItemId {
        val time = Clock.System.now().toEpochMilliseconds()
        val entityToInsert = entity.copy(
            creationTimestamp = time,
            updateTimestamp = time
        )

        val rowId = db.groceryListItemDao().insert(entityToInsert)
        require(rowId > 0)
        return GroceryListItemId(rowId)
    }

    override suspend fun update(entity: GroceryListItemRoomEntity) {
        val entityToUpdate = entity.copy(
            updateTimestamp = Clock.System.now().toEpochMilliseconds()
        )

        val rowsUpdated = db.groceryListItemDao().update(entityToUpdate)
        require(rowsUpdated == 1)
    }

    override suspend fun delete(entity: GroceryListItemRoomEntity) {
        val rowsDeleted = db.groceryListItemDao().delete(entity)
        require(rowsDeleted == 1)
    }

    override suspend fun getAllGroceryListItemsPaging(
        groceryListId: GroceryListId,
        offset: Int,
        pageSize: Int
    ): List<GroceryListItemRoomEntity> {
        return db.groceryListItemDao().getAllGroceryListItemsPaging(
            groceryListId.value,
            offset,
            pageSize
        )
    }

}
