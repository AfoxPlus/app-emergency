package com.afoxplus.emergency.presentation.register

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrationViewModelTest {
    @Test
    fun validPhoneMovesToCodeStep() {
        val viewModel = RegistrationViewModel(FakeRegistrationPreferences())

        viewModel.onPhoneChanged("900 000 000")
        viewModel.onContinueClicked()

        assertEquals(1, viewModel.uiState.value.step)
        assertEquals("900000000", viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun invalidPhoneShowsErrorAndDoesNotAdvance() {
        val viewModel = RegistrationViewModel(FakeRegistrationPreferences())

        viewModel.onPhoneChanged("123")
        viewModel.onContinueClicked()

        assertEquals(0, viewModel.uiState.value.step)
        assertEquals(RegistrationError.InvalidPhone, viewModel.uiState.value.error)
    }

    @Test
    fun completingDetailsPersistsRegistration() {
        val preferences = FakeRegistrationPreferences()
        val viewModel = RegistrationViewModel(preferences)

        viewModel.onPhoneChanged("900000000")
        viewModel.onContinueClicked()
        viewModel.onCodeChanged("123456")
        viewModel.onContinueClicked()
        viewModel.onFirstNameChanged("Ana")
        viewModel.onLastNameChanged("Pérez")
        viewModel.onContinueClicked()

        assertTrue(viewModel.uiState.value.isCompleted)
        assertTrue(preferences.isRegistrationCompleted())
    }
}
