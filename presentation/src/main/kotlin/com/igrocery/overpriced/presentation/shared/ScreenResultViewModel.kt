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

    fun setResult(result: ResultT) {
        savedStateHandle[ResultTag] = result
    }

    // result can only be retrieved once, this prevents the receiver from accidentally reprocessing
    // the result
    fun consumeResults() : ResultT? {
        val result: ResultT? = savedStateHandle[ResultTag]
        savedStateHandle[ResultTag] = null
        return result
    }

}
