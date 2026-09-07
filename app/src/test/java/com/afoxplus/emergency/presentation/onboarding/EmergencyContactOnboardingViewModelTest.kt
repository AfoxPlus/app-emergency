package com.afoxplus.emergency.presentation.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmergencyContactOnboardingViewModelTest {
    @Test
    fun `phone input keeps only nine digits and continue saves the contact`() {
        val repository = FakeEmergencyContactRepository()
        val viewModel = EmergencyContactOnboardingViewModel(repository)

        viewModel.onNameChanged(" Carlos Mendoza ")
        viewModel.onPhoneChanged("+51 987 654 321")
        viewModel.onContinueClicked()

        assertEquals("987654321", repository.addedContacts.single().phoneNumber)
        assertEquals("Carlos Mendoza", repository.addedContacts.single().name)
        assertTrue(viewModel.uiState.value.isCompleted)
    }

    @Test
    fun `continue reports an error until a valid contact is entered`() {
        val viewModel = EmergencyContactOnboardingViewModel(FakeEmergencyContactRepository())

        viewModel.onContinueClicked()

        assertFalse(viewModel.uiState.value.isCompleted)
        assertEquals(
            EmergencyContactOnboardingError.MissingContact,
            viewModel.uiState.value.error
        )
    }
}
