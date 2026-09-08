package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.presentation.contacts.FakeEmergencyContactRepository
import com.afoxplus.emergency.presentation.home.FakeSettingsPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerSosAlertUseCaseTest {

    private fun createUseCase(
        coordinates: Coordinates? = null,
        smsShouldSucceed: Boolean = true,
        contacts: List<Contact> = listOf(Contact(id = "1", name = "Mamá", phoneNumber = "987654321"))
    ): Triple<TriggerSosAlertUseCase, FakeLocationProvider, FakeSmsSender> {
        val locationProvider = FakeLocationProvider(coordinates)
        val smsSender = FakeSmsSender(shouldSucceed = smsShouldSucceed)
        val triggerAlertUseCase = TriggerAlertUseCase(
            emergencyContactRepository = FakeEmergencyContactRepository(contacts),
            settingsPreferences = FakeSettingsPreferences(),
            smsSender = smsSender,
            alertNotifier = FakeAlertNotifier(),
            alertHistoryRepository = FakeAlertHistoryRepository()
        )
        val useCase = TriggerSosAlertUseCase(
            locationProvider = locationProvider,
            triggerAlertUseCase = triggerAlertUseCase
        )
        return Triple(useCase, locationProvider, smsSender)
    }

    @Test
    fun `when location permission is granted, returns captured coordinates and sends the alert`() {
        val coordinates = Coordinates(latitude = -12.0464, longitude = -77.0428)
        val (useCase, locationProvider, smsSender) = createUseCase(coordinates = coordinates)

        val result = useCase()

        assertEquals(coordinates, result.coordinates)
        assertNotNull(result.historyEntryId)
        assertTrue(locationProvider.getCurrentLocationCalled)
        assertEquals(1, smsSender.sentMessages.size)
    }

    @Test
    fun `when location cannot be captured, returns null coordinates without blocking the alert`() {
        val (useCase, locationProvider, smsSender) = createUseCase(coordinates = null)

        val result = useCase()

        assertNull(result.coordinates)
        assertNotNull(result.historyEntryId)
        assertTrue(locationProvider.getCurrentLocationCalled)
        assertEquals(1, smsSender.sentMessages.size)
    }

    @Test
    fun `persists an SOS_BUTTON history entry when the alert is sent`() {
        val alertHistoryRepository = FakeAlertHistoryRepository()
        val useCase = TriggerSosAlertUseCase(
            locationProvider = FakeLocationProvider(null),
            triggerAlertUseCase = TriggerAlertUseCase(
                emergencyContactRepository = FakeEmergencyContactRepository(
                    listOf(Contact(id = "1", name = "Mamá", phoneNumber = "987654321"))
                ),
                settingsPreferences = FakeSettingsPreferences(),
                smsSender = FakeSmsSender(shouldSucceed = true),
                alertNotifier = FakeAlertNotifier(),
                alertHistoryRepository = alertHistoryRepository
            )
        )

        useCase()

        val history = alertHistoryRepository.getHistory()
        assertEquals(1, history.size)
        assertEquals(AlertType.SOS_BUTTON, history.first().type)
    }
}
