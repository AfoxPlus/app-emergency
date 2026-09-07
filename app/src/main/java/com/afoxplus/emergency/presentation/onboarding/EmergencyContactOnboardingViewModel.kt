package com.afoxplus.emergency.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class EmergencyContactOnboardingViewModel @Inject constructor(
    private val emergencyContactRepository: EmergencyContactRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmergencyContactOnboardingUiState())
    val uiState: StateFlow<EmergencyContactOnboardingUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, error = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update {
            it.copy(
                phoneNumber = value.filter(Char::isDigit).take(EmergencyContactOnboardingUiState.PHONE_DIGITS),
                error = null
            )
        }
    }

    fun onContinueClicked() {
        val state = _uiState.value
        if (!state.canContinue) {
            _uiState.update { it.copy(error = EmergencyContactOnboardingError.MissingContact) }
            return
        }

        emergencyContactRepository.addEmergencyContact(
            Contact(UUID.randomUUID().toString(), state.name.trim(), state.phoneNumber)
        )
        _uiState.update { it.copy(isCompleted = true) }
    }
}
