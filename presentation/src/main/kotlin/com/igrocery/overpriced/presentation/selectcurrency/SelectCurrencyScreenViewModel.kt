package com.igrocery.overpriced.presentation.selectcurrency

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class SelectCurrencyScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val preferenceService: PreferenceService
) : ViewModel() {

    val preferredCurrencyFlow = preferenceService.getAppPreference()
        .map {
            it.preferredCurrency
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun selectCurrency(currency: Currency) {
        viewModelScope.launch {
            preferenceService.updatePreferredCurrency(currency)
        }
    }

}
