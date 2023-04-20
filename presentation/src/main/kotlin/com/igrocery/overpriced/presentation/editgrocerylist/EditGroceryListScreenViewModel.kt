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
import com.igrocery.overpriced.presentation.shared.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface EditGroceryListScreenViewModelState {
    val groceryListFlow: StateFlow<LoadingState<GroceryList>>
    val groceryListItemFlow: Flow<PagingData<GroceryListItem>>
}

@HiltViewModel
class EditGroceryListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    groceryListService: GroceryListService,
) : ViewModel(), EditGroceryListScreenViewModelState {

    private val args = EditGroceryListScreenArgs(savedStateHandle)

    override val groceryListFlow = groceryListService.getGroceryList(args.groceryListId)
        .map { LoadingState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

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
