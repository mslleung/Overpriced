package com.igrocery.overpriced.presentation.editcategory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.models.*
import com.igrocery.overpriced.presentation.NavDestinations
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface EditCategoryScreenViewModelState {
    val categoryFlow: StateFlow<LoadingState<Category?>>
    val updateCategoryResult: LoadingState<Unit>
}

@HiltViewModel
class EditCategoryScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryService: CategoryService,
) : ViewModel(), EditCategoryScreenViewModelState {

    private val categoryId =
        savedStateHandle.get<Long>(NavDestinations.EditCategory_Arg_CategoryId).takeIf { it != 0L }

    override val categoryFlow = if (categoryId == null)
        MutableStateFlow<LoadingState<Category?>>(LoadingState.Success(null))
    else
        categoryService.getCategory(categoryId)
            .map {
                LoadingState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = LoadingState.Loading()
            )

    override var updateCategoryResult: LoadingState<Unit> by mutableStateOf(LoadingState.NotLoading())

    fun updateCategory(
        categoryName: String,
        categoryIcon: CategoryIcon,
    ) {
        val originalCategory = categoryFlow.value
        if (originalCategory is LoadingState.Success && originalCategory.data != null) {
            viewModelScope.launch {
                runCatching {
                    val updatedCategory = originalCategory.data.copy(
                        name = categoryName.trim(),
                        icon = categoryIcon,
                    )
                    categoryService.updateCategory(updatedCategory)

                    updateCategoryResult = LoadingState.Success(Unit)
                }.onFailure {
                    updateCategoryResult = LoadingState.Error(it)
                }
            }
        } else {
            log.error("Original category not loaded.")
        }
    }

    fun deleteCategory() {
        viewModelScope.launch {
            val category = categoryFlow.value
            if (category is LoadingState.Success && category.data != null) {
                categoryService.deleteCategory(category.data)

            }
        }
    }

}
