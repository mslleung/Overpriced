package com.igrocery.overpriced.presentation.selectcurrency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.presentation.shared.LoadingState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

interface SelectCurrencyScreenViewModelState {
    val preferredCurrencyFlow: StateFlow<LoadingState<Currency>>
}

@HiltViewModel
class SelectCurrencyScreenViewModel @Inject constructor(
    private val preferenceService: PreferenceService
) : ViewModel(), SelectCurrencyScreenViewModelState {

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

    fun selectCurrency(currency: Currency) {
        viewModelScope.launch {
            preferenceService.updatePreferredCurrency(currency)
        }
    }

}
