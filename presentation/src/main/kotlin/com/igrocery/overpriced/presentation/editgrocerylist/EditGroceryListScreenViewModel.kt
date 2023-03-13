package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.igrocery.overpriced.application.grocerylist.GroceryListService
import com.igrocery.overpriced.domain.grocerylist.models.GroceryList
import com.igrocery.overpriced.domain.grocerylist.models.GroceryListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface EditGroceryListScreenViewModelState {
    val groceryListFlow: StateFlow<GroceryList>
    val groceryListItemFlow: Flow<PagingData<GroceryListItem>>
}

@HiltViewModel
class EditGroceryListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    groceryListService: GroceryListService,
) : ViewModel(), EditGroceryListScreenViewModelState {

    private val args = EditGroceryListScreenArgs(savedStateHandle)

    override val groceryListFlow = groceryListService.

    override val groceryListItemFlow = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 30
        )
    ) {
        groceryListService.getAllGroceryListItemsPaging(args.groceryListId)
    }.flow
        .cachedIn(viewModelScope)

}
