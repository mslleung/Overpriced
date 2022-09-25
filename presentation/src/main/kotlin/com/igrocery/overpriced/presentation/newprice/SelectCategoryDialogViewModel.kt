package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import com.igrocery.overpriced.presentation.shared.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SelectCategoryDialogViewModel @Inject constructor(
    categoryService: CategoryService,
) : ViewModel() {

    class ViewModelState {
        var allCategoriesFlow: StateFlow<LoadingState<List<Category>>> by mutableStateOf(MutableStateFlow(LoadingState.Loading()))
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            allCategoriesFlow = categoryService.getAllCategories()
                .map { LoadingState.Success(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadingState.Loading()
                )
        }
    }

}
