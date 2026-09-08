package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.presentation.contacts.FakeEmergencyContactRepository
import com.afoxplus.emergency.presentation.home.FakeSettingsPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerAlertUseCaseTest {

    @Test
    fun `when no emergency contacts saved, notifies user and does not send SMS`() {
        val contactsRepository = FakeEmergencyContactRepository(emptyList())
        val settingsPreferences = FakeSettingsPreferences()
        val smsSender = FakeSmsSender()
        val alertNotifier = FakeAlertNotifier()
        val alertHistoryRepository = FakeAlertHistoryRepository()

        val useCase = TriggerAlertUseCase(
            emergencyContactRepository = contactsRepository,
            settingsPreferences = settingsPreferences,
            smsSender = smsSender,
            alertNotifier = alertNotifier,
            alertHistoryRepository = alertHistoryRepository
        )

        val result = useCase()

        assertFalse(result.success)
        assertNull(result.historyEntryId)
        assertTrue(alertNotifier.noContactsNotified)
        assertTrue(smsSender.sentMessages.isEmpty())
        assertNull(alertNotifier.alertSentNotifiedWith)
        assertNull(alertNotifier.alertFailedNotifiedWith)
        assertTrue(alertHistoryRepository.getHistory().isEmpty())
    }

    @Test
    fun `when emergency contacts saved and SMS succeeds, sends SMS to all contacts and notifies delivery confirmation`() {
        val contacts = listOf(
            Contact(id = "1", name = "Mamá", phoneNumber = "987654321"),
            Contact(id = "2", name = "Carlos", phoneNumber = "912345678")
        )
        val contactsRepository = FakeEmergencyContactRepository(contacts)
        val settingsPreferences = FakeSettingsPreferences(
            sosMessage = "¡Auxilio! Por favor ayúdenme."
        )
        val smsSender = FakeSmsSender(shouldSucceed = true)
        val alertNotifier = FakeAlertNotifier()
        val alertHistoryRepository = FakeAlertHistoryRepository()

        val useCase = TriggerAlertUseCase(
            emergencyContactRepository = contactsRepository,
            settingsPreferences = settingsPreferences,
            smsSender = smsSender,
            alertNotifier = alertNotifier,
            alertHistoryRepository = alertHistoryRepository
        )

        val result = useCase()

        assertTrue(result.success)
        assertNotNull(result.historyEntryId)
        assertFalse(alertNotifier.noContactsNotified)
        assertEquals(2, smsSender.sentMessages.size)
        assertEquals("987654321" to "¡Auxilio! Por favor ayúdenme.", smsSender.sentMessages[0])
        assertEquals("912345678" to "¡Auxilio! Por favor ayúdenme.", smsSender.sentMessages[1])
        assertEquals(listOf("Mamá", "Carlos"), alertNotifier.alertSentNotifiedWith)
        assertNull(alertNotifier.alertFailedNotifiedWith)

        val history = alertHistoryRepository.getHistory()
        assertEquals(1, history.size)
        val entry = history.first()
        assertEquals(AlertType.QUICK_ALERT, entry.type)
        assertEquals(listOf("Mamá", "Carlos"), entry.notifiedContacts.map { it.name })
    }

    @Test
    fun `when emergency contacts saved but SMS delivery fails, notifies error`() {
        val contacts = listOf(
            Contact(id = "1", name = "Mamá", phoneNumber = "987654321")
        )
        val contactsRepository = FakeEmergencyContactRepository(contacts)
        val settingsPreferences = FakeSettingsPreferences()
        val smsSender = FakeSmsSender(shouldSucceed = false)
        val alertNotifier = FakeAlertNotifier()
        val alertHistoryRepository = FakeAlertHistoryRepository()

        val useCase = TriggerAlertUseCase(
            emergencyContactRepository = contactsRepository,
            settingsPreferences = settingsPreferences,
            smsSender = smsSender,
            alertNotifier = alertNotifier,
            alertHistoryRepository = alertHistoryRepository
        )

        val result = useCase()

        assertFalse(result.success)
        assertFalse(alertNotifier.noContactsNotified)
        assertNull(alertNotifier.alertSentNotifiedWith)
        assertEquals("No fue posible enviar los mensajes de emergencia.", alertNotifier.alertFailedNotifiedWith)
    }
}
