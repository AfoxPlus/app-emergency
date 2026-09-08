package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.domain.model.EmergencyContactType
import com.afoxplus.emergency.domain.model.NotifiedContact
import com.afoxplus.emergency.domain.repository.AlertHistoryRepository
import com.afoxplus.emergency.domain.repository.AlertNotifier
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import com.afoxplus.emergency.domain.repository.SettingsPreferences
import com.afoxplus.emergency.domain.repository.SmsSender
import java.util.UUID
import javax.inject.Inject

/**
 * Outcome of triggering a Quick Alert: whether the SMS was successfully delivered to at
 * least one contact, and the id of the persisted [AlertHistoryEntry] (if any), so callers
 * can later update its status, e.g. when the user cancels the alert.
 */
data class AlertTriggerResult(
    val success: Boolean,
    val historyEntryId: String? = null
)

/**
 * Triggers an emergency Quick Alert by reading saved emergency contacts, sending
 * the preconfigured SOS message via SMS to all contacts, and delivering a confirmation
 * or error notification. If no contacts are registered, notifies the user without sending messages.
 * Every alert that is actually sent is persisted to the [AlertHistoryRepository], so it can be
 * reviewed later on the Alert History screen.
 */
class TriggerQuickAlertUseCase @Inject constructor(
    private val emergencyContactRepository: EmergencyContactRepository,
    private val settingsPreferences: SettingsPreferences,
    private val smsSender: SmsSender,
    private val alertNotifier: AlertNotifier,
    private val alertHistoryRepository: AlertHistoryRepository
) {
    operator fun invoke(
        alertType: AlertType = AlertType.QUICK_ALERT,
        coordinates: Coordinates? = null,
        description: String = defaultDescription(alertType)
    ): AlertTriggerResult {
        val contacts = emergencyContactRepository.getEmergencyContacts()
        if (contacts.isEmpty()) {
            alertNotifier.notifyNoContactsConfigured()
            return AlertTriggerResult(success = false)
        }

        val message = settingsPreferences.getSosMessage()
        val successfulContacts = contacts.filter { contact ->
            smsSender.sendSms(contact.phoneNumber, message).isSuccess
        }

        if (successfulContacts.isEmpty()) {
            alertNotifier.notifyAlertFailed("No fue posible enviar los mensajes de emergencia.")
            return AlertTriggerResult(success = false)
        }

        alertNotifier.notifyAlertSent(successfulContacts.map { it.name })

        val entryId = UUID.randomUUID().toString()
        alertHistoryRepository.addEntry(
            AlertHistoryEntry(
                id = entryId,
                type = alertType,
                status = AlertStatus.ISSUED,
                timestampMillis = System.currentTimeMillis(),
                description = description,
                coordinates = coordinates,
                notifiedContacts = successfulContacts.map {
                    NotifiedContact(
                        name = it.name,
                        relationship = when (it.type) {
                            EmergencyContactType.PRIMARY -> "Contacto Principal"
                            EmergencyContactType.BACKUP -> "Respaldo"
                        }
                    )
                }
            )
        )

        return AlertTriggerResult(success = true, historyEntryId = entryId)
    }

    companion object {
        fun defaultDescription(alertType: AlertType): String = when (alertType) {
            AlertType.SOS_BUTTON -> "Pulsación sostenida de botón de auxilio en pantalla principal"
            AlertType.QUICK_ALERT -> "3 pulsaciones consecutivas del botón de encendido"
            AlertType.PERIODIC_CHECK -> "Tiempo de espera agotado (5 min) sin confirmación de bienestar"
        }
    }
}
