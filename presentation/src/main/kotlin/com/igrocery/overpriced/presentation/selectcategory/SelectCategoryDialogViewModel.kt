package com.igrocery.overpriced.presentation.selectcategory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCategoryDialogViewModel @Inject constructor(
    categoryService: CategoryService,
) : ViewModel() {

    class ViewModelState {
        var isAllCategoriesLoaded by mutableStateOf(false)
        var allCategories by mutableStateOf(emptyList<Category>())
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            viewModelScope.launch {
                categoryService.getAllCategories()
                    .collect {
                        isAllCategoriesLoaded = true
                        allCategories = it
                    }
            }
        }
    }

}
