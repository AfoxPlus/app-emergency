package com.afoxplus.emergency.presentation.login

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {
    private val viewModel = LoginViewModel()

    @Test
    fun digitsAreLimitedToFourAndLoginCompletes() {
        repeat(5) { viewModel.onDigitClicked(it) }

        assertEquals("0123", viewModel.uiState.value.pin)
        assertTrue(viewModel.uiState.value.canLogin)

        viewModel.onLoginClicked()

        assertTrue(viewModel.uiState.value.isCompleted)
    }

    @Test
    fun loginWithIncompletePinShowsError() {
        viewModel.onDigitClicked(1)

        viewModel.onLoginClicked()

        assertFalse(viewModel.uiState.value.isCompleted)
        assertEquals(LoginError.InvalidPin, viewModel.uiState.value.error)
    }
}
