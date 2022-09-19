package com.igrocery.overpriced.presentation.shared

// Representing loading state is very common in the UI

// I wanted to call this LoadState, but there is a name conflict with the paging library

sealed interface LoadingState<T> {
    class Loading<T> : LoadingState<T>
    data class Success<T>(val data: T) : LoadingState<T>
    class Error<T>(throwable: Throwable) : LoadingState<T>
}
