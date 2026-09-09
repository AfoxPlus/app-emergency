package com.afoxplus.emergency.domain.repository

/**
 * Contract for notifying the user about emergency alert events and delivery confirmations.
 */
interface AlertNotifier {
    fun notifyAlertSent(contactNames: List<String>)
    fun notifyAlertFailed(errorMessage: String)
    fun notifyNoContactsConfigured()

    /**
     * Launches the alert success screen from a background context (e.g. a Service reacting to
     * a screen on/off broadcast) using a full-screen intent notification. Direct `startActivity`
     * calls from a Service are silently blocked by Android's background activity launch
     * restrictions (API 29+), so a full-screen intent is required to reliably show the UI even
     * when the device is locked.
     */
    fun notifyAlertSuccessScreen(historyEntryId: String?)
}
