package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.GroceryListItemId
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.createSimplePagingSource
import com.igrocery.overpriced.infrastructure.di.DataSourceModule
import com.igrocery.overpriced.infrastructure.di.IoDispatcher
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.ILocalGroceryListItemDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListItemRepository @Inject internal constructor(
    @DataSourceModule.LocalDataSource private val localGroceryListItemDataSource: ILocalGroceryListItemDataSource,
    private val transaction: Transaction,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : IGroceryListItemRepository {

    override suspend fun insert(item: GroceryListItem): GroceryListItemId {
        return transaction.execute {
            localGroceryListItemDataSource.insert(item.toData())
        }
    }

    override suspend fun update(item: GroceryListItem) {
        transaction.execute {
            localGroceryListItemDataSource.update(item.toData())
        }
    }

    override suspend fun delete(item: GroceryListItem) {
        transaction.execute {
            localGroceryListItemDataSource.delete(item.toData())
        }
    }

    override fun getAllGroceryListItemsPaging(groceryListId: GroceryListId): PagingSource<Int, GroceryListItem> {
        return createSimplePagingSource(
            localGroceryListItemDataSource,
            ioDispatcher
        ) { offset, loadSize ->
            localGroceryListItemDataSource.getAllGroceryListItemsPaging(
                groceryListId,
                offset,
                loadSize
            ).map { it.toDomain() }
        }
    }

}
