package com.igrocery.overpriced.presentation.categorydetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class CategoryDetailScreenStateHolder {

    var isLazyListPagingFirstLoad = true

    companion object {
        val Saver : Saver<CategoryDetailScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isLazyListPagingFirstLoad,
                )
            },
            restore = {
                CategoryDetailScreenStateHolder().apply {
                    isLazyListPagingFirstLoad = it[0] as Boolean
                }
            }
        )
    }
}

@Composable
fun rememberCategoryDetailScreenState() = rememberSaveable(
    stateSaver = CategoryDetailScreenStateHolder.Saver
) {
    // UiState is not designed to be mutable. It should NEVER be reassigned.
    // The only exception is activity config change and process recreation. Hence it is mutable.
    mutableStateOf(CategoryDetailScreenStateHolder())
}
