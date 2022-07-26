package com.igrocery.overpriced.presentation.categorylist

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

class CategoryListScreenStateHolder {

//    var isLazyListPagingFirstLoad = true

    companion object {
        val Saver = Saver<CategoryListScreenStateHolder, Bundle>(
            save = {
                Bundle().apply {
//                    putBoolean(KEY_IS_LAZY_LIST_PAGING_FIRST_LOAD, it.isLazyListPagingFirstLoad)
                }
            },
            restore = { bundle ->
                CategoryListScreenStateHolder().apply {
//                    isLazyListPagingFirstLoad = bundle.getBoolean(KEY_IS_LAZY_LIST_PAGING_FIRST_LOAD)
                }
            }
        )
    }
}

@Composable
fun rememberCategoryListScreenState() = rememberSaveable(
    stateSaver = CategoryListScreenStateHolder.Saver
) {
    // UiState is not designed to be mutable. It should NEVER be reassigned.
    // The only exception is activity config change and process recreation. Hence it is mutable.
    mutableStateOf(CategoryListScreenStateHolder())
}
