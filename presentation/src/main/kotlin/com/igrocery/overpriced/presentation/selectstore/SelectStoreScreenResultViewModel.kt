package com.igrocery.overpriced.presentation.selectstore

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.presentation.shared.ScreenResultViewModel
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@Parcelize
data class Result(
    val storeId: StoreId
) : Parcelable

@HiltViewModel
class SelectStoreScreenResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ScreenResultViewModel<Result>(savedStateHandle)
