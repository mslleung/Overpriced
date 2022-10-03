package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class SearchProductScreenStateHolder {
    var isRequestingFirstFocus by mutableStateOf(true)
    var query by mutableStateOf("")
}

@Composable
fun rememberSearchProductScreenState() = rememberSaveable(
    stateSaver = listSaver(
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
) {
    mutableStateOf(SearchProductScreenStateHolder())
}
