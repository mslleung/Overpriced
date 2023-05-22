package com.igrocery.overpriced.infrastructure.grocerylist.datasources.local

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.infrastructure.AppDatabase
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.daos.GroceryListDao
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.GroceryListRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalGroceryListDataSource @Inject internal constructor(
    private val db: AppDatabase,
) : ILocalGroceryListDataSource {

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

    override fun getGroceryList(id: GroceryListId): Flow<GroceryListRoomEntity?> {
        return db.groceryListDao().getGroceryList(id.value).distinctUntilChanged()
    }

    override fun getGroceryListCount(): Flow<Int> {
        return db.groceryListDao().getGroceryListCount()
    }

    override fun getAllGroceryListsWithItemCountPaging(): PagingSource<Int, GroceryListDao.GroceryListWithItemCount> {
        return db.groceryListDao().getGroceryListsWithItemCountPaging()
    }

}
