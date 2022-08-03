package com.igrocery.overpriced.presentation.categorydetail

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.igrocery.overpriced.presentation.searchproduct.SearchProductScreenStateHolder

class CategoryDetailScreenStateHolder {

    var isLazyListPagingFirstLoad = true

    companion object {
        val Saver : Saver<CategoryDetailScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isLazyListPagingFirstLoad,
//                    it.query,
                )
            },
            restore = {
                CategoryDetailScreenStateHolder().apply {
                    isLazyListPagingFirstLoad = it[0] as Boolean
//                    query = it[1] as String
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
