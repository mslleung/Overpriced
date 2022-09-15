package com.igrocery.overpriced.presentation.selectcurrency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igrocery.overpriced.application.preference.PreferenceService
import com.igrocery.overpriced.presentation.shared.LoadState
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@Suppress("unused")
private val log = Logger { }

@HiltViewModel
class SelectCurrencyScreenViewModel @Inject constructor(
    private val preferenceService: PreferenceService
) : ViewModel() {

    class ViewModelState {
        var preferredCurrencyFlow: StateFlow<LoadState<Currency>> by mutableStateOf(
            MutableStateFlow(LoadState.Loading())
        )
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            preferredCurrencyFlow = preferenceService.getAppPreference()
                .map { LoadState.Success(it.preferredCurrency) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = LoadState.Loading()
                )
        }
    }

    fun selectCurrency(currency: Currency) {
        viewModelScope.launch {
            preferenceService.updatePreferredCurrency(currency)
        }
    }

}
