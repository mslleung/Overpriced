package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCategoryScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val categoryService: CategoryService,
) : ViewModel() {

    var createCategoryResult by mutableStateOf<CreateCategoryResult?>(null)

    sealed interface CreateCategoryResult {
        data class Success(val categoryId: Long) : CreateCategoryResult
        data class Error(val throwable: Throwable) : CreateCategoryResult
    }

    fun createCategory(
        categoryName: String,
        categoryIcon: CategoryIcon,
    ) {
        viewModelScope.launch {
            runCatching {
                val newCategoryId = categoryService.createCategory(categoryIcon, categoryName)

                createCategoryResult = CreateCategoryResult.Success(newCategoryId)
            }.onFailure {
                createCategoryResult = CreateCategoryResult.Error(it)
            }
        }
    }

}
