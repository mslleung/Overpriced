package com.igrocery.overpriced.presentation.shared

import androidx.compose.runtime.*
import com.igrocery.overpriced.shared.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Suppress("unused")
private val log = Logger { }

// Composable functions to trigger recomposition periodically

@Composable
fun ticksEverySecond(): State<Int> {
    return produceState(initialValue = 0) {
        while (true) {
            delay(1000)

            if (isActive) {
                value += 1
            } else {
                break
            }
        }
    }
}
