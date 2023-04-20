package com.igrocery.overpriced.presentation.editcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon

class EditCategoryScreenStateHolder(
    isInitialized: Boolean,
    categoryName: String,
    categoryIcon: CategoryIcon,
    isConfirmDeleteDialogShown: Boolean
) {

    var isInitialized by mutableStateOf(isInitialized)
    var categoryName by mutableStateOf(categoryName)
    var categoryIcon by mutableStateOf(categoryIcon)
    var isConfirmDeleteDialogShown by mutableStateOf(isConfirmDeleteDialogShown)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isInitialized,
                    it.categoryName,
                    it.categoryIcon,
                    it.isConfirmDeleteDialogShown,
                )
            },
            restore = {
                EditCategoryScreenStateHolder(
                    it[0] as Boolean,
                    it[1] as String,
                    it[2] as CategoryIcon,
                    it[3] as Boolean
                )
            }
        )
    }
}

@Composable
fun rememberEditCategoryScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(EditCategoryScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(EditCategoryScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(
        EditCategoryScreenStateHolder(
            isInitialized = false,
            categoryName = "",
            categoryIcon = CategoryIcon.NoCategory,
            isConfirmDeleteDialogShown = false
        )
    )
}
