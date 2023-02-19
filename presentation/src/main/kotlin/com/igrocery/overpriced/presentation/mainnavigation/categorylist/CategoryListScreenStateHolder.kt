package com.igrocery.overpriced.presentation.mainnavigation.categorylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class CategoryListScreenStateHolder() {
    // placeholder...

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    false
                )
            },
            restore = {
                CategoryListScreenStateHolder()
            }
        )
    }
}

@Composable
fun rememberCategoryListScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(CategoryListScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(CategoryListScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(CategoryListScreenStateHolder())
}
