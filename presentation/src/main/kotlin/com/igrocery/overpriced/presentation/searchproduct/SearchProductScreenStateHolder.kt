package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class SearchProductScreenStateHolder(savedState: List<*>? = null) {

    var isRequestingFirstFocus by mutableStateOf(savedState?.get(0) as? Boolean ?: true)
    var query by mutableStateOf(savedState?.get(1) as? String ?:"")

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
        restore = { savedState ->
            SearchProductScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(SearchProductScreenStateHolder())
}
