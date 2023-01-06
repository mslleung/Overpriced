package com.igrocery.overpriced.presentation.mainnavigation.grocerylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.presentation.shared.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface GroceryListScreenViewModelState {
    val categoryWithProductCountFlow: StateFlow<LoadingState<List<CategoryWithProductCount>>>
}

@HiltViewModel
class GroceryListScreenViewModel @Inject constructor(
    categoryService: CategoryService,
) : ViewModel(), GroceryListScreenViewModelState {

    override val categoryWithProductCountFlow: StateFlow<LoadingState<List<CategoryWithProductCount>>> =
        categoryService.getAllCategoriesWithProductCount()
            .map {
                LoadingState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = LoadingState.Loading()
            )

}
