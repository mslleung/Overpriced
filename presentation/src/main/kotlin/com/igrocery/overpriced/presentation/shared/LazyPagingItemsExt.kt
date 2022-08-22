package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.*
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> LazyPagingItems<T>.isInitialLoadCompleted(): Boolean {
    var isFirstLoadTriggered by remember { mutableStateOf(false) }
    if (!isFirstLoadTriggered) {
        LaunchedEffect(key1 = loadState.refresh) {
            if (loadState.refresh is LoadState.Loading) {
                isFirstLoadTriggered = true
            }
        }
    }

    var isFirstLoadCompleted by remember { mutableStateOf(false) }
    if (isFirstLoadTriggered && !isFirstLoadCompleted) {
        LaunchedEffect(key1 = loadState.refresh) {
            if (loadState.refresh is LoadState.NotLoading) {
                isFirstLoadCompleted = true
            }
        }
    }

    return isFirstLoadTriggered || isFirstLoadCompleted
}
