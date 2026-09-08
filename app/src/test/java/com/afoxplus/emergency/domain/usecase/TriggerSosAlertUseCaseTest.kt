package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.model.Coordinates
import com.afoxplus.emergency.presentation.contacts.FakeEmergencyContactRepository
import com.afoxplus.emergency.presentation.home.FakeSettingsPreferences
import org.junit.Assert.assertEquals
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
        val triggerQuickAlertUseCase = TriggerQuickAlertUseCase(
            emergencyContactRepository = FakeEmergencyContactRepository(contacts),
            settingsPreferences = FakeSettingsPreferences(),
            smsSender = smsSender,
            alertNotifier = FakeAlertNotifier()
        )
        val useCase = TriggerSosAlertUseCase(
            locationProvider = locationProvider,
            triggerQuickAlertUseCase = triggerQuickAlertUseCase
        )
        return Triple(useCase, locationProvider, smsSender)
    }

    @Test
    fun `when location permission is granted, returns captured coordinates and sends the alert`() {
        val coordinates = Coordinates(latitude = -12.0464, longitude = -77.0428)
        val (useCase, locationProvider, smsSender) = createUseCase(coordinates = coordinates)

        val result = useCase()

        assertEquals(coordinates, result)
        assertTrue(locationProvider.getCurrentLocationCalled)
        assertEquals(1, smsSender.sentMessages.size)
    }

    @Test
    fun `when location cannot be captured, returns null coordinates without blocking the alert`() {
        val (useCase, locationProvider, smsSender) = createUseCase(coordinates = null)

        val result = useCase()

        assertNull(result)
        assertTrue(locationProvider.getCurrentLocationCalled)
        assertEquals(1, smsSender.sentMessages.size)
    }
}
