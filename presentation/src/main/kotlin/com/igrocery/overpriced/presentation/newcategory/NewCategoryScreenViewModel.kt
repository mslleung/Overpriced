package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import com.igrocery.overpriced.presentation.shared.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface NewCategoryScreenViewModelState {
    val createCategoryResult: LoadingState<CategoryId>
}

@HiltViewModel
class NewCategoryScreenViewModel @Inject constructor(
    private val categoryService: CategoryService,
) : ViewModel(), NewCategoryScreenViewModelState {

    override var createCategoryResult by mutableStateOf<LoadingState<CategoryId>>(LoadingState.NotLoading())

    fun createCategory(
        categoryName: String,
        categoryIcon: CategoryIcon,
    ) {
        viewModelScope.launch {
            runCatching {
                val newCategoryId = categoryService.createCategory(categoryIcon, categoryName)

                createCategoryResult = LoadingState.Success(newCategoryId)
            }.onFailure {
                createCategoryResult = LoadingState.Error(it)
            }
        }
    }

}
