package com.igrocery.overpriced.presentation.editcategory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.domain.productpricehistory.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EditCategoryScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val categoryService: CategoryService,
) : ViewModel() {

    private companion object {
        private const val KEY_CATEGORY_ID = "KEY_CATEGORY_ID"
    }

    val categoryFlow = savedState.getStateFlow(KEY_CATEGORY_ID, 0L)
        .flatMapLatest { categoryService.getCategoryById(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun setCategoryId(id: Long) {
        savedState[KEY_CATEGORY_ID] = id
    }

    var updateCategoryResult by mutableStateOf<UpdateCategoryResult?>(null)

    sealed interface UpdateCategoryResult {
        object Success : UpdateCategoryResult
        data class Error(val throwable: Throwable) : UpdateCategoryResult
    }

    fun updateCategory(
        categoryName: String,
        categoryIcon: CategoryIcon,
    ) {
        viewModelScope.launch {
            runCatching {
                val originalCategory = categoryFlow.value!!
                val updatedCategory = Category(
                    id = originalCategory.id,
                    creationTimestamp = originalCategory.creationTimestamp,
                    updateTimestamp = System.currentTimeMillis(),
                    name = categoryName,
                    icon = categoryIcon,
                )
                categoryService.updateCategory(updatedCategory)

                updateCategoryResult = UpdateCategoryResult.Success
            }.onFailure {
                updateCategoryResult = UpdateCategoryResult.Error(it)
            }
        }
    }

}
