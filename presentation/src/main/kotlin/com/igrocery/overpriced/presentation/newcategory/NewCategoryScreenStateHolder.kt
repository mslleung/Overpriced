package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon

class NewCategoryScreenStateHolder {

    var isRequestingFirstFocus by mutableStateOf(true)
    var categoryName by mutableStateOf("")
    var categoryIcon by mutableStateOf(CategoryIcon.NoCategory)

    companion object {
        val Saver: Saver<NewCategoryScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.categoryName,
                    it.categoryIcon.name
                )
            },
            restore = {
                NewCategoryScreenStateHolder().apply {
                    isRequestingFirstFocus = it[0] as Boolean
                    categoryName = it[1] as String
                    categoryIcon = CategoryIcon.valueOf(it[2] as String)
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
