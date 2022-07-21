package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class NewCategoryScreenStateHolder {

    var categoryName by mutableStateOf("")

    companion object {
        val Saver: Saver<NewCategoryScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                )
            },
            restore = {
                NewCategoryScreenStateHolder().apply {
                    isRequestingFirstFocus = it[0] as Boolean
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
