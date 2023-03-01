package com.igrocery.overpriced.presentation.selectcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

class SelectCategoryScreenStateHolder {

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    false
                )
            },
            restore = {
                SelectCategoryScreenStateHolder(
                )
            }
        )
    }
}
@Composable
fun rememberSelectCategoryScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(SelectCategoryScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(SelectCategoryScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(SelectCategoryScreenStateHolder())
}
