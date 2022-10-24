package com.igrocery.overpriced.presentation.editcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.productpricehistory.models.CategoryIcon

class EditCategoryScreenStateHolder(savedState: List<*>? = null) {

    var isInitialized by mutableStateOf(savedState?.get(0) as? Boolean ?: false)
    var categoryName by mutableStateOf(savedState?.get(1) as? String ?: "")
    var categoryIcon by mutableStateOf(
        savedState?.get(2) as? CategoryIcon ?: CategoryIcon.NoCategory
    )

    var isConfirmDeleteDialogShown by mutableStateOf(savedState?.get(3) as? Boolean ?: false)

}

@Composable
fun rememberEditCategoryScreenState() = rememberSaveable(
    stateSaver = listSaver(
        save = {
            listOf(
                it.isInitialized,
                it.categoryName,
                it.categoryIcon,
                it.isConfirmDeleteDialogShown,
            )
        },
        restore = { savedState ->
            EditCategoryScreenStateHolder(savedState)
        }
    )
) {
    mutableStateOf(EditCategoryScreenStateHolder())
}
