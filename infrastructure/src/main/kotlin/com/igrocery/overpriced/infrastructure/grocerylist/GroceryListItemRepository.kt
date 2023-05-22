package com.igrocery.overpriced.infrastructure.grocerylist

import androidx.paging.PagingSource
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.GroceryListItemId
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem
import com.igrocery.overpriced.infrastructure.MappedPagingSource
import com.igrocery.overpriced.infrastructure.Transaction
import com.igrocery.overpriced.infrastructure.di.DataSourceModule
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.ILocalGroceryListItemDataSource
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toData
import com.igrocery.overpriced.infrastructure.grocerylist.datasources.local.entities.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryListItemRepository @Inject internal constructor(
    @DataSourceModule.LocalDataSource private val localGroceryListItemDataSource: ILocalGroceryListItemDataSource,
    private val transaction: Transaction,
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
        return MappedPagingSource(
            dataPagingSource = localGroceryListItemDataSource.getAllGroceryListItemsPaging(
                groceryListId
            ),
            mapper = { it.toDomain() }
        )
    }

}
