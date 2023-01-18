package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.grocerylist.GroceryListService
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface MainBottomNavigationScreenViewModelState {
    var createNewGroceryListResultState: LoadingState<Long>

    fun createNewGroceryList()
}

@HiltViewModel
class MainBottomNavigationScreenViewModel @Inject constructor(
    private val groceryListService: GroceryListService,
) : ViewModel(), MainBottomNavigationScreenViewModelState {

    override var createNewGroceryListResultState: LoadingState<Long> by mutableStateOf(LoadingState.NotLoading())

    override fun createNewGroceryList() {
        viewModelScope.launch {
            createNewGroceryListResultState = LoadingState.Loading()
            createNewGroceryListResultState = try {
                val id = groceryListService.createNewGroceryList()
                LoadingState.Success(id)
            } catch (e: Exception) {
                log.error(e.toString())
                LoadingState.Error(e)
            }
        }
    }

}
