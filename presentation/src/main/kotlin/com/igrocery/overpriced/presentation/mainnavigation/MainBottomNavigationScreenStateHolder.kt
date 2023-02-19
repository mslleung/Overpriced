package com.igrocery.overpriced.presentation.mainnavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MainBottomNavigationScreenStateHolder(
    shouldShowFabForCategoryListScreen: Boolean,
    isGroceryListNameDialogShown: Boolean
) {

    var shouldShowFabForCategoryListScreen by mutableStateOf(shouldShowFabForCategoryListScreen)

    var isGroceryListNameDialogShown by mutableStateOf(isGroceryListNameDialogShown)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.shouldShowFabForCategoryListScreen,
                    it.isGroceryListNameDialogShown
                )
            },
            restore = {
                MainBottomNavigationScreenStateHolder(
                    it[0] as Boolean,
                    it[1] as Boolean,
                )
            }
        )
    }
}

@Composable
fun rememberMainBottomNavigationScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(MainBottomNavigationScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(MainBottomNavigationScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(
        MainBottomNavigationScreenStateHolder(
            shouldShowFabForCategoryListScreen = true, // TODO
            isGroceryListNameDialogShown = false
        )
    )
}