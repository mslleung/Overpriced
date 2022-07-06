package com.igrocery.overpriced.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.util.*

class SettingsScreenStateHolder {

    companion object {
        val Saver: Saver<SettingsScreenStateHolder, *> = listSaver(
            save = {
                listOf(
                    null
                )
            },
            restore = {
                SettingsScreenStateHolder().apply {

                }
            }
        )
    }
}

@Composable
fun rememberSettingsScreenState() = rememberSaveable(
    stateSaver = SettingsScreenStateHolder.Saver
) {
    mutableStateOf(SettingsScreenStateHolder())
}
