package com.igrocery.overpriced.presentation.categorylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class CategoryListScreenStateHolder {
    // placeholder...
}

@Composable
fun rememberCategoryListScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                false
            )
        },
        restore = {
            CategoryListScreenStateHolder().apply {

            }
        }
    )
) {
    // UiState is not designed to be mutable. It should NEVER be reassigned.
    // The only exception is activity config change and process recreation. Hence it is mutable.
    mutableStateOf(CategoryListScreenStateHolder())
}
