package com.igrocery.overpriced.presentation.selectcategory

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCategoryDialogViewModel @Inject constructor(
    categoryService: CategoryService,
) : ViewModel() {

    @Stable
    data class ViewModelState(
        val isAllCategoriesLoaded: Boolean = false,
        val allCategories: List<Category> = emptyList()
    )

    var uiState by mutableStateOf(ViewModelState())
        private set

    init {
        viewModelScope.launch {
            categoryService.getAllCategories()
                .collect {
                    uiState = uiState.copy(
                        isAllCategoriesLoaded = true,
                        allCategories = it
                    )
                }
        }
    }

}
