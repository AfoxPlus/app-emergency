package com.afoxplus.emergency.presentation.main

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.presentation.contacts.FakeEmergencyContactRepository
import com.afoxplus.emergency.presentation.navigation.AlertSuccessRoute
import com.afoxplus.emergency.presentation.navigation.EmergencyContactOnboardingRoute
import com.afoxplus.emergency.presentation.navigation.LoginRoute
import com.afoxplus.emergency.presentation.onboarding.FakeOnboardingPreferences
import com.afoxplus.emergency.presentation.register.FakeRegistrationPreferences
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    @Test
    fun `onAlertLaunchIntent clears the back stack and shows the alert success route`() {
        val viewModel = MainViewModel(
            onboardingPreferences = FakeOnboardingPreferences(initiallyCompleted = true),
            registrationPreferences = FakeRegistrationPreferences(completed = true),
            emergencyContactRepository = FakeEmergencyContactRepository(
                listOf(Contact(id = "1", name = "Mamá", phoneNumber = "987654321"))
            )
        )

        viewModel.onAlertLaunchIntent(
            latitude = -12.0464,
            longitude = -77.0428,
            historyEntryId = "history-1"
        )

        assertEquals(
            listOf(
                AlertSuccessRoute(
                    latitude = -12.0464,
                    longitude = -77.0428,
                    historyEntryId = "history-1"
                )
            ),
            viewModel.backStack.toList()
        )
    }

    @Test
    fun `completed registration without emergency contacts starts contact onboarding`() {
        val viewModel = MainViewModel(
            onboardingPreferences = FakeOnboardingPreferences(initiallyCompleted = true),
            registrationPreferences = FakeRegistrationPreferences(completed = true),
            emergencyContactRepository = FakeEmergencyContactRepository()
        )

        assertEquals(listOf(EmergencyContactOnboardingRoute), viewModel.backStack.toList())
    }

    @Test
    fun `completed onboarding and registration with contacts starts login`() {
        val viewModel = MainViewModel(
            onboardingPreferences = FakeOnboardingPreferences(initiallyCompleted = true),
            registrationPreferences = FakeRegistrationPreferences(completed = true),
            emergencyContactRepository = FakeEmergencyContactRepository(
                listOf(Contact(id = "1", name = "Mamá", phoneNumber = "987654321"))
            )
        )

        assertEquals(listOf(LoginRoute), viewModel.backStack.toList())
    }
}
