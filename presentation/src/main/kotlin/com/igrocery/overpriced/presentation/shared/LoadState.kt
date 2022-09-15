package com.igrocery.overpriced.presentation.shared

// Representing loading state is very common in the UI

sealed interface LoadState<T> {
    class Loading<T> : LoadState<T>
    data class Success<T>(val data: T) : LoadState<T>
    class Error<T>(throwable: Throwable) : LoadState<T>
}
