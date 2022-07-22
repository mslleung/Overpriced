package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class NewCategoryScreenStateHolder {

    var isRequestingFirstFocus by mutableStateOf(true)
    var categoryName by mutableStateOf("")

    companion object {
        val Saver: Saver<NewCategoryScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.categoryName,
                )
            },
            restore = {
                NewCategoryScreenStateHolder().apply {
                    isRequestingFirstFocus = it[0] as Boolean
                    categoryName = it[1] as String
                }
            }
        )
    }
}

@Composable
fun rememberNewCategoryScreenState() = rememberSaveable(
    stateSaver = NewCategoryScreenStateHolder.Saver
) {
    mutableStateOf(NewCategoryScreenStateHolder())
}
