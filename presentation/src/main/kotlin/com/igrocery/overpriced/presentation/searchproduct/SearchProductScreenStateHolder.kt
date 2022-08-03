package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class SearchProductScreenStateHolder {

    var isRequestingFirstFocus by mutableStateOf(true)
    var query by mutableStateOf("")

    companion object {
        val Saver : Saver<SearchProductScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.query,
                )
            },
            restore = {
                SearchProductScreenStateHolder().apply {
                    isRequestingFirstFocus = it[0] as Boolean
                    query = it[1] as String
                }
            }
        )
    }
}

@Composable
fun rememberSearchProductScreenState() = rememberSaveable(
    stateSaver = SearchProductScreenStateHolder.Saver
) {
    // UiState is not designed to be mutable. It should NEVER be reassigned.
    // The only exception is activity config change and process recreation. Hence it is mutable.
    mutableStateOf(SearchProductScreenStateHolder())
}
