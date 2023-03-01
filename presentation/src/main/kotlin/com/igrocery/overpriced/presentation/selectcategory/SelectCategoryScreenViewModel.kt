package com.igrocery.overpriced.presentation.selectcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface SelectCategoryScreenViewModelState {
    var allCategoriesFlow: StateFlow<List<Category>>
}

@HiltViewModel
class SelectCategoryScreenViewModel @Inject constructor(
    categoryService: CategoryService,
) : ViewModel(), SelectCategoryScreenViewModelState {

    override var allCategoriesFlow = categoryService.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
