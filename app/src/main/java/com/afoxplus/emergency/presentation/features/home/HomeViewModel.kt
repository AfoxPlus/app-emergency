package com.afoxplus.emergency.presentation.features.home

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.domain.repository.EmergencyContactsCountProvider
import com.afoxplus.emergency.domain.repository.PeriodicCheckPreferences
import com.afoxplus.emergency.domain.repository.QuickAlertManager
import com.afoxplus.emergency.domain.repository.RegistrationPreferences
import com.afoxplus.emergency.domain.repository.SettingsPreferences
import com.afoxplus.emergency.domain.usecase.SosAlertResult
import com.afoxplus.emergency.domain.usecase.TriggerSosAlertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val registrationPreferences: RegistrationPreferences,
    private val settingsPreferences: SettingsPreferences,
    private val periodicCheckPreferences: PeriodicCheckPreferences,
    private val emergencyContactsCountProvider: EmergencyContactsCountProvider,
    private val quickAlertManager: QuickAlertManager,
    private val triggerSosAlertUseCase: TriggerSosAlertUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private fun loadInitialState(): HomeUiState = HomeUiState(
        userName = registrationPreferences.getName(),
        isQuickAlertEnabled = settingsPreferences.isQuickAlertEnabled(),
        isPeriodicCheckEnabled = periodicCheckPreferences.getConfiguration().enabled,
        emergencyContactsCount = emergencyContactsCountProvider.getContactsCount()
    )

    fun onResume(hasContactsPermission: Boolean, hasLocationPermission: Boolean) {
        _uiState.update {
            it.copy(
                userName = registrationPreferences.getName(),
                isQuickAlertEnabled = settingsPreferences.isQuickAlertEnabled(),
                isPeriodicCheckEnabled = periodicCheckPreferences.getConfiguration().enabled,
                emergencyContactsCount = emergencyContactsCountProvider.getContactsCount(),
                hasContactsPermission = hasContactsPermission,
                hasLocationPermission = hasLocationPermission
            )
        }
    }

    fun onQuickAlertToggled(enabled: Boolean) {
        settingsPreferences.setQuickAlertEnabled(enabled)
        quickAlertManager.setQuickAlertEnabled(enabled)
        _uiState.update {
            it.copy(
                isQuickAlertEnabled = enabled,
                snackbarMessage = if (enabled) {
                    "Alerta rápida activada. Tus contactos de emergencia serán notificados si se activa."
                } else null
            )
        }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    /**
     * Sends an SOS alert to the saved emergency contacts (reusing the Quick Alert sending
     * logic) and attempts to capture the current location. Returns the [SosAlertResult] with
     * the captured [Coordinates] (or `null` if they could not be resolved, without blocking
     * the alert) and the id of the persisted Alert History entry, if any.
     */
    fun triggerSosAlert(): SosAlertResult = triggerSosAlertUseCase()
}
