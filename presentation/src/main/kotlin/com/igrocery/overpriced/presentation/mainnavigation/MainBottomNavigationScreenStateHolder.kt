package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MainBottomNavigationScreenStateHolder(savedState: List<*>? = null) {

    var shouldShowFabForCategoryListScreen by mutableStateOf( // TODO
        savedState?.get(0) as? Boolean ?: true
    )

    var isGroceryListNameDialogShown by mutableStateOf(savedState?.get(1) as? Boolean ?: false)

}

@Composable
fun rememberMainBottomNavigationScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.shouldShowFabForCategoryListScreen,
                it.isGroceryListNameDialogShown
            )
        },
        restore = { savedState ->
            MainBottomNavigationScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(MainBottomNavigationScreenStateHolder())
}