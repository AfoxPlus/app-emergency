package com.afoxplus.emergency.presentation.settings

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.presentation.register.RegistrationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Owns the Settings screen UI state: real profile data, the persisted SOS emergency
 * message (with its "Emergency Message" bottom sheet editing flow) and the status of the
 * 4 System Permissions, which is reported by [SettingsScreen] after checking the real OS
 * permission state.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val registrationPreferences: RegistrationPreferences,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            name = registrationPreferences.getName(),
            phoneNumber = registrationPreferences.getPhoneNumber(),
            sosMessage = settingsPreferences.getSosMessage()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /** Reports the current real OS status for every System Permission (Settings AC04). */
    fun onPermissionsChecked(statuses: Map<SettingsPermissionType, Boolean>) {
        _uiState.update { it.copy(permissions = it.permissions + statuses) }
    }

    /** Reports a single permission result, e.g. after the user answers the runtime request. */
    fun onPermissionResult(type: SettingsPermissionType, granted: Boolean) {
        _uiState.update { it.copy(permissions = it.permissions + (type to granted)) }
    }

    /** Opens the "Emergency Message" bottom sheet, loading the currently saved message (AC06/AC07). */
    fun onEditMessageClicked() {
        _uiState.update {
            it.copy(isMessageSheetVisible = true, draftMessage = it.sosMessage)
        }
    }

    fun onDraftMessageChanged(value: String) {
        _uiState.update { it.copy(draftMessage = value) }
    }

    /** Closes the bottom sheet without persisting any change to the saved message (AC10). */
    fun onDismissMessageSheet() {
        _uiState.update { it.copy(isMessageSheetVisible = false) }
    }

    /** Persists the edited message and reflects it immediately in the SMS preview (AC09). */
    fun onSaveMessageClicked() {
        val message = _uiState.value.draftMessage
        settingsPreferences.saveSosMessage(message)
        _uiState.update { it.copy(sosMessage = message, isMessageSheetVisible = false) }
    }
}
