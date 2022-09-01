package com.igrocery.overpriced.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val preferenceService: PreferenceService
) : ViewModel() {

    class ViewModelState {
        var isPreferredCurrencyLoaded by mutableStateOf(false)
        var preferredCurrency: Currency by mutableStateOf(Currency.getInstance(Locale.getDefault()))
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            viewModelScope.launch {
                preferenceService.getAppPreference()
                    .map { it.preferredCurrency }
                    .collect {
                        isPreferredCurrencyLoaded = true
                        preferredCurrency = it
                    }
            }
        }
    }
}
