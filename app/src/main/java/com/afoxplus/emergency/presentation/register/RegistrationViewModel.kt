package com.afoxplus.emergency.presentation.register

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.repository.RegistrationPreferences
import com.afoxplus.emergency.presentation.register.RegistrationUiState.Companion.PHONE_DIGITS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val preferences: RegistrationPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        RegistrationUiState(isCompleted = preferences.isRegistrationCompleted())
    )
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onPhoneChanged(value: String) {
        _uiState.update {
            it.copy(
                phoneNumber = value.filter(Char::isDigit).take(PHONE_DIGITS),
                error = null
            )
        }
    }

    fun onCodeChanged(value: String) {
        _uiState.update {
            it.copy(
                verificationCode = value.filter(Char::isDigit).take(RegistrationUiState.VERIFICATION_CODE_LENGTH),
                error = null
            )
        }
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { it.copy(firstName = value, error = null) }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { it.copy(lastName = value, error = null) }
    }

    fun onContinueClicked() {
        val state = _uiState.value
        if (!state.canContinue) {
            _uiState.update {
                it.copy(
                    error = when (it.step) {
                        0 -> RegistrationError.InvalidPhone
                        1 -> RegistrationError.InvalidCode
                        else -> RegistrationError.MissingName
                    }
                )
            }
            return
        }

        if (state.step == LAST_STEP) {
            preferences.saveProfile(state.fullName, state.phoneNumber)
            preferences.setRegistrationCompleted(true)
            _uiState.update { it.copy(isCompleted = true) }
        } else {
            _uiState.update { it.copy(step = it.step + 1) }
        }
    }

    fun onBackClicked() {
        _uiState.update { state ->
            if (state.step == 0) state else state.copy(step = state.step - 1, error = null)
        }
    }

    private companion object {
        const val LAST_STEP = 2
    }
}
