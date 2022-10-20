package com.igrocery.overpriced.presentation.shared

import android.os.Parcelable
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

// Representing loading state is very common in the UI. This generic class can be used to handle
// most cases. Please try to use it for dealing with load states.

// I wanted to call this LoadState, but there is a name conflict with the paging library

sealed interface LoadingState<T>: Parcelable {
    @Parcelize
    class NotLoading<T>: LoadingState<T>    // special starting state
    @Parcelize
    class Loading<T> : LoadingState<T>
    @Parcelize
    data class Success<T>(val data: @RawValue T) : LoadingState<T>
    @Parcelize
    data class Error<T>(val throwable: Throwable) : LoadingState<T>
}

@Composable
fun <T> LoadingState<T>.ifLoading(content: @Composable () -> Unit): LoadingState<T> {
    if (this is LoadingState.Loading) {
        content()
    }
    return this
}

@Composable
fun <T> LoadingState<T>.ifLoaded(content: @Composable (T) -> Unit): LoadingState<T> {
    if (this is LoadingState.Success) {
        content(data)
    }
    return this
}

@Composable
fun <T> LoadingState<T>.ifLoadFailed(content: @Composable (Throwable) -> Unit): LoadingState<T> {
    if (this is LoadingState.Error) {
        content(throwable)
    }
    return this
}
