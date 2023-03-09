package com.igrocery.overpriced.presentation.selectstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igrocery.overpriced.domain.StoreId

class SelectStoreScreenStateHolder(
    selectedStoreId: StoreId?
) {
    var selectedStoreId by mutableStateOf(selectedStoreId)

    companion object {
        fun Saver() = listSaver(
            save = {
                listOf(
                    it.selectedStoreId,
                )
            },
            restore = {
                SelectStoreScreenStateHolder(
                    it[0] as StoreId?,
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
    mutableStateOf(SelectStoreScreenStateHolder(args.selectedStoreId))
}
