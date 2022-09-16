package com.igrocery.overpriced.presentation.shared

// Representing loading state is very common in the UI

sealed interface LoadingState<T> {
    class Loading<T> : LoadingState<T>
    data class Success<T>(val data: T) : LoadingState<T>
    class Error<T>(throwable: Throwable) : LoadingState<T>
}
