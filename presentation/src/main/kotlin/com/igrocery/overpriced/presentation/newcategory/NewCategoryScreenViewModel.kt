package com.igrocery.overpriced.presentation.newcategory

import androidx.lifecycle.SavedStateHandle
import com.igrocery.overpriced.application.productpricehistory.CategoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewCategoryScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val categoryService: CategoryService,
) {



}
