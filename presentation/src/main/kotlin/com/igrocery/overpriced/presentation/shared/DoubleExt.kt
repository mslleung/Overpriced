package com.igrocery.overpriced.presentation.shared

fun Double.format(digits: Int) = "%.${digits}f".format(this)
