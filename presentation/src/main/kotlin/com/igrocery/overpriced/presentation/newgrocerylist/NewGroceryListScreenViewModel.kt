package com.igrocery.overpriced.presentation.newgrocerylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.grocerylist.GroceryListService
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface NewGroceryListScreenViewModelState {
    val groceryListsWithItemCountFlow: Flow<PagingData<GroceryListWithItemCount>>
}

@HiltViewModel
class NewGroceryListScreenViewModel @Inject constructor(
    groceryListService: GroceryListService,
) : ViewModel(), NewGroceryListScreenViewModelState {

    override val groceryListsWithItemCountFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        groceryListService.getAllGroceryListsWithItemCountPaging()
    }.flow
        .cachedIn(viewModelScope)

}
