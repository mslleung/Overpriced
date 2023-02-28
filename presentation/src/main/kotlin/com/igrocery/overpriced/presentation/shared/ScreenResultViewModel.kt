package com.igrocery.overpriced.presentation.shared

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Shared view model for returning results to previous fragment.
 */

private const val ResultTag = "result"

abstract class ScreenResultViewModel<ResultT : Parcelable>(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var result: ResultT?
        get() = savedStateHandle[ResultTag]
        set(value) {
            savedStateHandle[ResultTag] = value
        }

    fun clear() {
        result = null
    }
}
