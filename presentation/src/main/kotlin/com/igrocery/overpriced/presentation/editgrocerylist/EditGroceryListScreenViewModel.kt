package com.igrocery.overpriced.presentation.editgrocerylist

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

interface EditGroceryListScreenViewModelState {
    val groceryListsWithItemCountFlow: Flow<PagingData<GroceryListWithItemCount>>
}

@HiltViewModel
class EditGroceryListScreenViewModel @Inject constructor(
    groceryListService: GroceryListService,
) : ViewModel(), EditGroceryListScreenViewModelState {

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