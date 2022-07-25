package com.igrocery.overpriced.presentation.editcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon

class EditCategoryScreenStateHolder {

    var isInitialized by mutableStateOf(false)
    var categoryName by mutableStateOf("")
    var categoryIcon by mutableStateOf(CategoryIcon.Uncategorized)

    companion object {
        val Saver: Saver<EditCategoryScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.isInitialized,
                    it.categoryName,
                    it.categoryIcon.name
                )
            },
            restore = {
                EditCategoryScreenStateHolder().apply {
                    isInitialized = it[0] as Boolean
                    categoryName = it[1] as String
                    categoryIcon = CategoryIcon.valueOf(it[2] as String)
                }
            }
        )
    }
}

@Composable
fun rememberEditCategoryScreenState() = rememberSaveable(
    stateSaver = EditCategoryScreenStateHolder.Saver
) {
    mutableStateOf(EditCategoryScreenStateHolder())
}
