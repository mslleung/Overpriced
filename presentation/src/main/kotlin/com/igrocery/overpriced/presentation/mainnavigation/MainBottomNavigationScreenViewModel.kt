package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.igrocery.overpriced.application.grocerylist.GroceryListService
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.domain.grocerylist.dtos.GroceryListWithItemCount
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface MainBottomNavigationScreenViewModelState {
    val groceryListCountFlow: StateFlow<LoadingState<Int>>

    var createNewGroceryListResultState: LoadingState<GroceryListId>

    fun createNewGroceryList(groceryListName: String)
}

@HiltViewModel
class MainBottomNavigationScreenViewModel @Inject constructor(
    private val groceryListService: GroceryListService,
) : ViewModel(), MainBottomNavigationScreenViewModelState {

    override val groceryListCountFlow = groceryListService.getGroceryListCount()
        .map {
            LoadingState.Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LoadingState.Loading()
        )

    override var createNewGroceryListResultState: LoadingState<GroceryListId> by mutableStateOf(LoadingState.NotLoading())

    override fun createNewGroceryList(groceryListName: String) {
        viewModelScope.launch {
            createNewGroceryListResultState = LoadingState.Loading()
            createNewGroceryListResultState = try {
                val id = groceryListService.createNewGroceryList(groceryListName)
                LoadingState.Success(id)
            } catch (e: Exception) {
                log.error(e.toString())
                LoadingState.Error(e)
            }
        }
    }

}
