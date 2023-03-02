package com.igrocery.overpriced.presentation.selectcategory

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.CategoryId
import kotlinx.parcelize.Parcelize

class SelectCategoryScreenStateHolder(
    selectedCategoryId: CategoryId?,
    categoryMoreDialogData: CategoryMoreDialogData?,
) {

    var selectedCategoryId by mutableStateOf(selectedCategoryId)

    @Parcelize
    data class CategoryMoreDialogData(
        val categoryId: CategoryId
    ) : Parcelable

    var categoryMoreDialogData by mutableStateOf(categoryMoreDialogData)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.selectedCategoryId,
                    it.categoryMoreDialogData
                )
            },
            restore = {
                SelectCategoryScreenStateHolder(
                    it[0] as CategoryId?,
                    it[1] as CategoryMoreDialogData?
                )
            }
        )
    }
}

@Composable
internal fun rememberSelectCategoryScreenState(args: SelectCategoryScreenArgs) = rememberSaveable(
    stateSaver = Saver(
        save = { with(SelectCategoryScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(SelectCategoryScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(SelectCategoryScreenStateHolder(args.selectedCategoryId, null))
}
