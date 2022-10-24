package com.igrocery.overpriced.presentation.newcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon

class NewCategoryScreenStateHolder(savedState: List<*>? = null) {

    var isRequestingFirstFocus by mutableStateOf(savedState?.get(0) as? Boolean ?: true)
    var categoryName by mutableStateOf(savedState?.get(1) as? String ?: "")
    var categoryIcon by mutableStateOf(
        savedState?.get(2) as? CategoryIcon ?: CategoryIcon.NoCategory
    )

}

@Composable
fun rememberNewCategoryScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.isRequestingFirstFocus,
                it.categoryName,
                it.categoryIcon
            )
        },
        restore = { savedState ->
            NewCategoryScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(NewCategoryScreenStateHolder())
}
