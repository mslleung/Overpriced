package com.igrocery.overpriced.presentation.mainnavigation.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.dtos.CategoryWithProductCount
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface CategoryListScreenViewModelState {
    val categoryWithProductCountFlow: StateFlow<LoadingState<List<CategoryWithProductCount>>>
}

@HiltViewModel
class CategoryListScreenViewModel @Inject constructor(
    categoryService: CategoryService,
) : ViewModel(), CategoryListScreenViewModelState {

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
