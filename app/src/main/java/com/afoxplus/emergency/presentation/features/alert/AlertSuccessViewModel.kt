package com.afoxplus.emergency.presentation.features.alert

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.repository.AlertHistoryRepository
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import com.afoxplus.emergency.domain.repository.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Owns the Alert Activated screen UI state (current emergency contacts plus the persisted SOS
 * message) and marks the corresponding Alert History entry as cancelled when the user taps
 * "Cancelar alerta" before completion.
 */
@HiltViewModel
class AlertSuccessViewModel @Inject constructor(
    private val alertHistoryRepository: AlertHistoryRepository,
    private val emergencyContactRepository: EmergencyContactRepository,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertSuccessUiState())
    val uiState: StateFlow<AlertSuccessUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = AlertSuccessUiState(
            contacts = emergencyContactRepository.getEmergencyContacts(),
            sosMessage = settingsPreferences.getSosMessage()
        )
    }

    fun onCancelAlert(historyEntryId: String?) {
        if (historyEntryId != null) {
            alertHistoryRepository.updateStatus(historyEntryId, AlertStatus.CANCELLED)
        }
    }
}
