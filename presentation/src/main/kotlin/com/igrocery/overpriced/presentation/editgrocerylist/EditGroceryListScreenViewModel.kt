package com.igrocery.overpriced.presentation.editgrocerylist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.igrocery.overpriced.presentation.shared.requireLoaded
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface EditGroceryListScreenViewModelState {
    val groceryListFlow: StateFlow<LoadingState<GroceryList>>
    val groceryListItemFlow: Flow<PagingData<GroceryListItem>>

    var editGroceryListResultState: LoadingState<Unit>
    var deleteGroceryListResultState: LoadingState<Unit>
}

@HiltViewModel
class EditGroceryListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val groceryListService: GroceryListService,
) : ViewModel(), EditGroceryListScreenViewModelState {

    private val args = EditGroceryListScreenArgs(savedStateHandle)

    override val groceryListFlow = groceryListService.getGroceryList(args.groceryListId)
        .map {
            if (it != null)
                LoadingState.Success(it)
            else
                LoadingState.Error(Exception("Grocery list not found."))
        }
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

    override var editGroceryListResultState: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())
    override var deleteGroceryListResultState: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())

    fun editGroceryList(editedGroceryList: GroceryList) {
        viewModelScope.launch {
            editGroceryListResultState = LoadingState.Loading()
            editGroceryListResultState = try {
                groceryListService.editGroceryList(editedGroceryList)
                LoadingState.Success(Unit)
            } catch (e: Exception) {
                log.error(e.toString())
                LoadingState.Error(e)
            }
        }
    }

    fun deleteGroceryList() {
        viewModelScope.launch {
            deleteGroceryListResultState = LoadingState.Loading()
            deleteGroceryListResultState = try {
                val groceryList = groceryListFlow.first().requireLoaded()
                groceryListService.deleteGroceryList(groceryList)
                LoadingState.Success(Unit)
            } catch (e: Exception) {
                log.error(e.toString())
                LoadingState.Error(e)
            }
        }
    }

    fun addItem(itemName: String, itemDescription: String) {
        viewModelScope.launch {
            groceryListService.addItemToGroceryList(args.groceryListId, itemName, itemDescription)
        }
    }

    fun updateItem(item: GroceryListItem) {
        viewModelScope.launch {
            groceryListService.updateGroceryListItem(item)
        }
    }

    fun deleteItem(item: GroceryListItem) {
        viewModelScope.launch {
            groceryListService.deleteGroceryListItem(item)
        }
    }

}
