package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.grocerylist.GroceryListService
import com.igrocery.overpriced.domain.GroceryListId
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface MainBottomNavigationScreenViewModelState {
    var createNewGroceryListResultState: LoadingState<GroceryListId>

    fun createNewGroceryList(groceryListName: String)
}

@HiltViewModel
class MainBottomNavigationScreenViewModel @Inject constructor(
    private val groceryListService: GroceryListService,
) : ViewModel(), MainBottomNavigationScreenViewModelState {

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
