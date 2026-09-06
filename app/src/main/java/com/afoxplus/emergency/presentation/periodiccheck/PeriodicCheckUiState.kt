package com.afoxplus.emergency.presentation.periodiccheck

/**
 * Immutable UI state for the Periodic Safety Check screen.
 */
data class PeriodicCheckUiState(
    val enabled: Boolean = false,
    val frequencyMinutes: Int = PeriodicCheckConfiguration.DEFAULT_FREQUENCY_MINUTES,
    val responseTimeoutMinutes: Int = PeriodicCheckConfiguration.DEFAULT_RESPONSE_TIMEOUT_MINUTES,
    val emergencyContactsCount: Int = 0
) {
    /** Highlights the predefined option matching [frequencyMinutes], if any (AC06). */
    val selectedFrequencyOption: FrequencyOption?
        get() = FrequencyOption.fromMinutes(frequencyMinutes)

    val selectedResponseTimeOption: ResponseTimeOption?
        get() = ResponseTimeOption.fromMinutes(responseTimeoutMinutes)

    /** AC17/BR15: Emergency Contacts are mandatory to activate the periodic check. */
    val hasEmergencyContacts: Boolean
        get() = emergencyContactsCount > 0

    val canActivate: Boolean
        get() = hasEmergencyContacts && frequencyMinutes > 0 && responseTimeoutMinutes > 0
}
