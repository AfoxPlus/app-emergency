package com.afoxplus.emergency.presentation.periodiccheck

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.model.PeriodicCheckConfiguration
import com.afoxplus.emergency.domain.repository.EmergencyContactsCountProvider
import com.afoxplus.emergency.domain.repository.PeriodicCheckPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Owns the Periodic Safety Check UI state: frequency and response-time selection,
 * Emergency Contacts summary and activation, persisting the configuration through
 * [PeriodicCheckPreferences]. Business/validation logic lives here, not in the Composables.
 */
@HiltViewModel
class PeriodicCheckViewModel @Inject constructor(
    private val preferences: PeriodicCheckPreferences,
    private val emergencyContactsCountProvider: EmergencyContactsCountProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<PeriodicCheckUiState> = _uiState.asStateFlow()

    private fun loadInitialState(): PeriodicCheckUiState {
        val configuration = preferences.getConfiguration()
        return PeriodicCheckUiState(
            enabled = configuration.enabled,
            frequencyMinutes = configuration.frequencyMinutes,
            responseTimeoutMinutes = configuration.responseTimeoutMinutes,
            emergencyContactsCount = emergencyContactsCountProvider.getContactsCount()
        )
    }

    /** AC04: selecting a predefined frequency becomes the active configuration. */
    fun onFrequencyOptionSelected(option: FrequencyOption) {
        updateFrequency(option.minutes)
    }

    /** AC05: increases the custom frequency, clamped to [PeriodicCheckConfiguration.MAX_FREQUENCY_MINUTES]. */
    fun onCustomFrequencyIncrement() {
        val next = (_uiState.value.frequencyMinutes + PeriodicCheckConfiguration.FREQUENCY_STEP_MINUTES)
            .coerceAtMost(PeriodicCheckConfiguration.MAX_FREQUENCY_MINUTES)
        updateFrequency(next)
    }

    /** AC05: decreases the custom frequency, clamped to [PeriodicCheckConfiguration.MIN_FREQUENCY_MINUTES]. */
    fun onCustomFrequencyDecrement() {
        val next = (_uiState.value.frequencyMinutes - PeriodicCheckConfiguration.FREQUENCY_STEP_MINUTES)
            .coerceAtLeast(PeriodicCheckConfiguration.MIN_FREQUENCY_MINUTES)
        updateFrequency(next)
    }

    /** AC08: selecting a response time becomes the active configuration. */
    fun onResponseTimeOptionSelected(option: ResponseTimeOption) {
        _uiState.update { it.copy(responseTimeoutMinutes = option.minutes) }
        persist()
    }

    /** AC16: refreshes the Emergency Contacts count, e.g. after returning from that screen. */
    fun onEmergencyContactsRefreshRequested() {
        _uiState.update { it.copy(emergencyContactsCount = emergencyContactsCountProvider.getContactsCount()) }
    }

    /** AC10/AC17: activation is only allowed when the configuration is valid. */
    fun onActivateClicked() {
        if (!_uiState.value.canActivate) return
        _uiState.update { it.copy(enabled = true) }
        persist()
    }

    fun onDeactivateClicked() {
        _uiState.update { it.copy(enabled = false) }
        persist()
    }

    private fun updateFrequency(minutes: Int) {
        _uiState.update { it.copy(frequencyMinutes = minutes) }
        persist()
    }

    private fun persist() {
        val state = _uiState.value
        preferences.saveConfiguration(
            PeriodicCheckConfiguration(
                enabled = state.enabled,
                frequencyMinutes = state.frequencyMinutes,
                responseTimeoutMinutes = state.responseTimeoutMinutes,
                lastConfigurationUpdate = System.currentTimeMillis()
            )
        )
    }
}
