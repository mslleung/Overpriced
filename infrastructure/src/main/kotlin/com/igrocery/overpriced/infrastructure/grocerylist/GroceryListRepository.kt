package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.createSimplePagingSource
import com.igrocery.overpriced.infrastructure.di.DataSourceModule
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.ILocalGroceryListDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListRepository @Inject internal constructor(
    @DataSourceModule.LocalDataSource private val localGroceryListDataSource: ILocalGroceryListDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IGroceryListRepository {

    override suspend fun insert(item: GroceryList): Long {
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

    override fun getAllGroceryListsWithItemCountPaging(): PagingSource<Int, GroceryListWithItemCount> {
        return createSimplePagingSource(
            localGroceryListDataSource,
            ioDispatcher
        ) { offset, loadSize ->
            localGroceryListDataSource.getAllGroceryListsWithItemCountPaging(
                offset,
                loadSize
            ).map {
                GroceryListWithItemCount(
                    it.groceryListRoomEntity.toDomain(),
                    it.itemCount
                )
            }
        }
    }

}
