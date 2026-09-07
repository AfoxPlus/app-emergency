package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.repository.AlertNotifier
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import com.afoxplus.emergency.domain.repository.SettingsPreferences
import com.afoxplus.emergency.domain.repository.SmsSender
import javax.inject.Inject

/**
 * Triggers an emergency Quick Alert by reading saved emergency contacts, sending
 * the preconfigured SOS message via SMS to all contacts, and delivering a confirmation
 * or error notification. If no contacts are registered, notifies the user without sending messages.
 */
class TriggerQuickAlertUseCase @Inject constructor(
    private val emergencyContactRepository: EmergencyContactRepository,
    private val settingsPreferences: SettingsPreferences,
    private val smsSender: SmsSender,
    private val alertNotifier: AlertNotifier
) {
    operator fun invoke(): Boolean {
        val contacts = emergencyContactRepository.getEmergencyContacts()
        if (contacts.isEmpty()) {
            alertNotifier.notifyNoContactsConfigured()
            return false
        }

        val message = settingsPreferences.getSosMessage()
        val successfulContacts = mutableListOf<String>()

        for (contact in contacts) {
            val result = smsSender.sendSms(contact.phoneNumber, message)
            if (result.isSuccess) {
                successfulContacts.add(contact.name)
            }
        }

        return if (successfulContacts.isNotEmpty()) {
            alertNotifier.notifyAlertSent(successfulContacts)
            true
        } else {
            alertNotifier.notifyAlertFailed("No fue posible enviar los mensajes de emergencia.")
            false
        }
    }
}
