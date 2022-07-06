package com.igrocery.overpriced.presentation.newprice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class SelectStoreDialogStateHolder(selectedStoreId: Long) {

    var selectedStoreId by mutableStateOf(selectedStoreId)

    companion object {
        val Saver: Saver<SelectStoreDialogStateHolder, *> = listSaver(
            save = {
                listOf(
                    it.selectedStoreId
                )
            },
            restore = {
                SelectStoreDialogStateHolder(
                    selectedStoreId = it[0]
                )
            }
        )
    }

}

@Composable
fun rememberSelectStoreDialogState(selectedStoreId: Long) = rememberSaveable(
    selectedStoreId,
    stateSaver = SelectStoreDialogStateHolder.Saver
) {
    mutableStateOf(SelectStoreDialogStateHolder(selectedStoreId))
}
