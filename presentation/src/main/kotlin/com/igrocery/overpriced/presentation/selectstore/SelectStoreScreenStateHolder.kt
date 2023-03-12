package com.igrocery.overpriced.presentation.selectstore

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.StoreId
import com.igrocery.overpriced.domain.productpricehistory.models.Store
import kotlinx.parcelize.Parcelize

class SelectStoreScreenStateHolder(
    selectedStoreId: StoreId?,
    storeMoreDialogData: StoreMoreDialogData?,
    deleteStoreDialogData: DeleteStoreDialogData?

) {
    var selectedStoreId by mutableStateOf(selectedStoreId)

    @Parcelize
    data class StoreMoreDialogData(
        val store: Store
    ) : Parcelable

    var storeMoreDialogData by mutableStateOf(storeMoreDialogData)

    @Parcelize
    data class DeleteStoreDialogData(
        val store: Store
    ) : Parcelable

    var deleteStoreDialogData by mutableStateOf(deleteStoreDialogData)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.selectedStoreId,
                    it.storeMoreDialogData,
                    it.deleteStoreDialogData
                )
            },
            restore = {
                SelectStoreScreenStateHolder(
                    it[0] as StoreId?,
                    it[1] as StoreMoreDialogData?,
                    it[2] as DeleteStoreDialogData?
                )
            }
        )
    }
}

@Composable
internal fun rememberSelectStoreScreenState(args: SelectStoreScreenArgs) = rememberSaveable(
    stateSaver = Saver(
        save = { with(SelectStoreScreenStateHolder.Saver()) { save(it) } },
        restore = { value -> with(SelectStoreScreenStateHolder.Saver()) { restore(value)!! } }
    )
) {
    mutableStateOf(SelectStoreScreenStateHolder(args.selectedStoreId, null, null))
}
