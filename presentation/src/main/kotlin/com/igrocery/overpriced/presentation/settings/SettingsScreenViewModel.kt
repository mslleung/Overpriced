package com.igrocery.overpriced.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface SettingsScreenViewModelState {
    val preferredCurrencyFlow: StateFlow<LoadingState<Currency>>
}

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val preferenceService: PreferenceService
) : ViewModel(), SettingsScreenViewModelState {

    override val preferredCurrencyFlow: StateFlow<LoadingState<Currency>>
        get() = preferenceService.getAppPreference()
            .map {
                LoadingState.Success(it.preferredCurrency)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = LoadingState.Loading()
            )

}
