package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.infrastructure.MappedPagingSource
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.ILocalGroceryListDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListRepository @Inject internal constructor(
    @DataSourceModule.LocalDataSource private val localGroceryListDataSource: ILocalGroceryListDataSource,
    private val transaction: Transaction,
) : IGroceryListRepository {

    override suspend fun insert(item: GroceryList): GroceryListId {
        return transaction.execute {
            localGroceryListDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: GroceryList) {
        transaction.execute {
            localGroceryListDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: GroceryList) {
        transaction.execute {
            localGroceryListDataSource.delete(item.toData())
        }
    }

    override fun getGroceryList(id: GroceryListId): Flow<GroceryList?> {
        return localGroceryListDataSource.getGroceryList(id)
            .map { it?.toDomain() }
    }

    override fun getGroceryListCount(): Flow<Int> {
        return localGroceryListDataSource.getGroceryListCount()
    }

    override fun getAllGroceryListsWithItemCountPaging(
        onDataSourcesInvalidated: PagingSource<Int, GroceryListWithItemCount>.() -> Unit
    ): PagingSource<Int, GroceryListWithItemCount> {
        return MappedPagingSource(
            dataPagingSource = localGroceryListDataSource.getAllGroceryListsWithItemCountPaging(),
            mapper = {
                GroceryListWithItemCount(
                    groceryList = it.groceryListRoomEntity.toDomain(),
                    checkedItemCount = it.checkedItemCount,
                    totalItemCount = it.totalItemCount
                )
            }
        )
    }

}
