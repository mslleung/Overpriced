package com.igrocery.overpriced.domain.preference.models

import java.util.*

data class AppPreference(
    val preferredCurrency: Currency =
        Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance(Locale.US)
)
