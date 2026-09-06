package com.afoxplus.emergency.presentation.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onDigitClicked(digit: Int) {
        if (digit !in 0..9) return
        _uiState.update {
            if (it.pin.length >= LoginUiState.PIN_LENGTH) it
            else it.copy(pin = it.pin + digit, error = null)
        }
    }

    fun onDeleteClicked() {
        _uiState.update { it.copy(pin = it.pin.dropLast(1), error = null) }
    }

    fun onLoginClicked() {
        _uiState.update {
            if (it.canLogin) it.copy(isCompleted = true)
            else it.copy(error = LoginError.InvalidPin)
        }
    }
}
