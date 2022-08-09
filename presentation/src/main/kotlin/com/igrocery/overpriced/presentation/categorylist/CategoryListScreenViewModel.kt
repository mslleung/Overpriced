package com.igrocery.overpriced.presentation.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import com.igrocery.overpriced.application.productpricehistory.ProductService
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class CategoryListScreenViewModel @Inject constructor(
//    private val savedState: SavedStateHandle,
    categoryService: CategoryService,
) : ViewModel() {

    val categoryWithProductCount = categoryService.getAllCategoriesWithProductCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

}
