package com.afoxplus.emergency.presentation.features.login

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.repository.PinPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val pinPreferences: PinPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LoginUiState(
            mode = if (pinPreferences.hasPin()) LoginMode.SignIn else LoginMode.PinRegistration
        )
    )
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

    fun onPrimaryActionClicked() {
        val state = _uiState.value
        if (!state.canSubmit) {
            _uiState.update { it.copy(error = LoginError.InvalidPin) }
            return
        }
        when (state.mode) {
            LoginMode.SignIn -> handleSignIn(state.pin)
            LoginMode.PinRegistration -> handlePinRegistration(state)
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }

    private fun handleSignIn(pin: String) {
        if (pin == pinPreferences.getPin()) {
            _uiState.update { it.copy(isCompleted = true) }
        } else {
            _uiState.update { it.copy(pin = "", error = LoginError.IncorrectPin) }
        }
    }

    private fun handlePinRegistration(state: LoginUiState) {
        when (state.stage) {
            PinRegistrationStage.EnterPin -> {
                _uiState.update {
                    it.copy(
                        firstPin = state.pin,
                        pin = "",
                        stage = PinRegistrationStage.ConfirmPin,
                        error = null
                    )
                }
            }

            PinRegistrationStage.ConfirmPin -> {
                if (state.pin == state.firstPin) {
                    pinPreferences.savePin(state.pin)
                    _uiState.update { it.copy(isCompleted = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            pin = "",
                            firstPin = "",
                            stage = PinRegistrationStage.EnterPin,
                            error = LoginError.PinMismatch
                        )
                    }
                }
            }
        }
    }
}
