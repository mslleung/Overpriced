package com.igrocery.overpriced.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val preferenceService: PreferenceService
) : ViewModel() {

    class ViewModelState {
        var preferredCurrency: LoadingState<Currency> by mutableStateOf(LoadingState.Loading())
    }

    val uiState = ViewModelState()

    init {
        with(uiState) {
            preferenceService.getAppPreference()
                .onEach {
                    preferredCurrency = LoadingState.Success(it.preferredCurrency)
                }
                .launchIn(viewModelScope)
        }
    }
}
