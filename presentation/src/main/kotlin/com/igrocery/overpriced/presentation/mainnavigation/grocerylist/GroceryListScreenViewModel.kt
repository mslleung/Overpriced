package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.grocerylist.GroceryListService
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface GroceryListScreenViewModelState {
    val groceryListsWithItemCountFlow: Flow<PagingData<GroceryListWithItemCount>>
}

@HiltViewModel
class GroceryListScreenViewModel @Inject constructor(
    groceryListService: GroceryListService,
) : ViewModel(), GroceryListScreenViewModelState {

    override val groceryListsWithItemCountFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        groceryListService.getAllGroceryListsWithItemCountPaging(
            onDataSourcesInvalidated = {
                invalidate()
            }
        )
    }.flow
        .cachedIn(viewModelScope)

}
