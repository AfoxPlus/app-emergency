package com.afoxplus.emergency.domain.repository

/**
 * Contract for notifying the user about emergency alert events and delivery confirmations.
 */
interface AlertNotifier {
    fun notifyAlertSent(contactNames: List<String>)
    fun notifyAlertFailed(errorMessage: String)
    fun notifyNoContactsConfigured()
}
