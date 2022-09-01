package com.igrocery.overpriced.presentation.searchproduct

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreenStateHolder

class SearchProductScreenStateHolder {
    val isRequestingFirstFocus: Boolean = true
    val query: String = ""
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
            SearchProductScreenStateHolder(
                isRequestingFirstFocus = it[0] as Boolean,
                query = it[1] as String
            )
        }
    )
) {
    mutableStateOf(SearchProductScreenStateHolder())
}
