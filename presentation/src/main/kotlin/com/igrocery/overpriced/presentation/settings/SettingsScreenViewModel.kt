package com.igrocery.overpriced.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val preferenceService: PreferenceService
) : ViewModel() {

    class ViewModelState {
        lateinit var preferredCurrencyFlow: StateFlow<Currency?>
    }

    val uiState = ViewModelState().apply {
        preferredCurrencyFlow =  preferenceService.getAppPreference()
            .map { it.preferredCurrency }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )
    }
}
