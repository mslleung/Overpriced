package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon

class NewCategoryScreenStateHolder(
    isRequestingFirstFocus: Boolean,
    categoryName: String,
    categoryIcon: CategoryIcon,
) {

    var isRequestingFirstFocus by mutableStateOf(isRequestingFirstFocus)
    var categoryName by mutableStateOf(categoryName)
    var categoryIcon by mutableStateOf(categoryIcon)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.isRequestingFirstFocus,
                    it.categoryName,
                    it.categoryIcon
                )
            },
            restore = {
                NewCategoryScreenStateHolder(
                    it[0] as Boolean,
                    it[1] as String,
                    it[2] as CategoryIcon
                )
            }
        )
    }
}

@Composable
fun rememberNewCategoryScreenState() = rememberSaveable(
    stateSaver = Saver(
        save = { with(NewCategoryScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(NewCategoryScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(
        NewCategoryScreenStateHolder(
            isRequestingFirstFocus = true,
            categoryName = "",
            categoryIcon = CategoryIcon.NoCategory
        )
    )
}
