package com.igrocery.overpriced.presentation.selectcategory

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.igrocery.overpriced.domain.CategoryId
import com.igrocery.overpriced.presentation.shared.ScreenResultViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class Result(
    val categoryId: CategoryId
) : Parcelable

@HiltViewModel
class SelectCategoryScreenResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ScreenResultViewModel<Result>(savedStateHandle)
