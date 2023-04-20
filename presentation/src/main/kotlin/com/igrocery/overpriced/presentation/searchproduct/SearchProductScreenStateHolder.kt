package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class SearchProductScreenStateHolder(
    isRequestingFirstFocus: Boolean,
    query: String
) {

    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)
    var query by mutableStateOf(query)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.query,
                )
            },
            restore = {
                SearchProductScreenStateHolder(
                    it[0] as Boolean,
                    it[1] as String,
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
                query = ""
            )
        )
    }
}
