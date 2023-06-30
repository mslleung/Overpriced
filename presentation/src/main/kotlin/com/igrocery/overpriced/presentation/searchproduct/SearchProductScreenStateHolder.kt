package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class SearchProductScreenStateHolder(
    isRequestingFirstFocus: Boolean,
) {

    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                )
            },
            restore = {
                SearchProductScreenStateHolder(
                    it[0] as Boolean,
                )
            }
        )
    }
}

@Composable
fun rememberSearchProductScreenState(): MutableState<SearchProductScreenStateHolder> {
    return rememberSaveable(
        stateSaver = Saver(
            save = { with(SearchProductScreenStateHolder.Saver()) { save(it) } },
            restore = { value -> with(SearchProductScreenStateHolder.Saver()) { restore(value)!! } }
        )
    ) {
        mutableStateOf(
            SearchProductScreenStateHolder(
                isRequestingFirstFocus = true,
            )
        )
    }
}
