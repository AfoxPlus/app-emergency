package com.afoxplus.emergency.presentation.login

import com.afoxplus.emergency.domain.repository.PinPreferences
import com.afoxplus.emergency.presentation.features.login.LoginError
import com.afoxplus.emergency.presentation.features.login.LoginMode
import com.afoxplus.emergency.presentation.features.login.LoginViewModel
import com.afoxplus.emergency.presentation.features.login.PinRegistrationStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakePinPreferences(private var pin: String? = null) : PinPreferences {
    override fun hasPin(): Boolean = pin != null
    override fun savePin(pin: String) {
        this.pin = pin
    }

    override fun getPin(): String = pin.orEmpty()
}

class LoginViewModelTest {

    @Test
    fun whenNoPinIsStoredViewModelStartsInRegistrationMode() {
        val viewModel = LoginViewModel(FakePinPreferences())

        assertEquals(LoginMode.PinRegistration, viewModel.uiState.value.mode)
        assertEquals(PinRegistrationStage.EnterPin, viewModel.uiState.value.stage)
    }

    @Test
    fun registeringAPinRequiresMatchingConfirmationAndPersistsIt() {
        val pinPreferences = FakePinPreferences()
        val viewModel = LoginViewModel(pinPreferences)

        repeat(4) { viewModel.onDigitClicked(it + 1) }
        assertTrue(viewModel.uiState.value.canSubmit)

        viewModel.onPrimaryActionClicked()
        assertEquals(PinRegistrationStage.ConfirmPin, viewModel.uiState.value.stage)
        assertEquals("", viewModel.uiState.value.pin)
        assertFalse(viewModel.uiState.value.isCompleted)

        repeat(4) { viewModel.onDigitClicked(it + 1) }
        viewModel.onPrimaryActionClicked()

        assertTrue(viewModel.uiState.value.isCompleted)
        assertEquals("1234", pinPreferences.getPin())
    }

    @Test
    fun registeringAPinWithMismatchedConfirmationShowsErrorAndRestartsFlow() {
        val pinPreferences = FakePinPreferences()
        val viewModel = LoginViewModel(pinPreferences)

        repeat(4) { viewModel.onDigitClicked(it + 1) }
        viewModel.onPrimaryActionClicked()

        repeat(4) { viewModel.onDigitClicked(it + 5) }
        viewModel.onPrimaryActionClicked()

        assertEquals(LoginError.PinMismatch, viewModel.uiState.value.error)
        assertEquals(PinRegistrationStage.EnterPin, viewModel.uiState.value.stage)
        assertFalse(viewModel.uiState.value.isCompleted)
        assertFalse(pinPreferences.hasPin())
    }

    @Test
    fun primaryActionWithIncompletePinShowsError() {
        val viewModel = LoginViewModel(FakePinPreferences())

        viewModel.onDigitClicked(1)
        viewModel.onPrimaryActionClicked()

        assertFalse(viewModel.uiState.value.isCompleted)
        assertEquals(LoginError.InvalidPin, viewModel.uiState.value.error)
    }

    @Test
    fun whenPinIsStoredViewModelStartsInSignInModeAndCorrectPinCompletesLogin() {
        val pinPreferences = FakePinPreferences("1234")
        val viewModel = LoginViewModel(pinPreferences)

        assertEquals(LoginMode.SignIn, viewModel.uiState.value.mode)

        repeat(4) { viewModel.onDigitClicked(it + 1) }
        viewModel.onPrimaryActionClicked()

        assertTrue(viewModel.uiState.value.isCompleted)
    }

    @Test
    fun signInWithIncorrectPinShowsErrorAndDoesNotComplete() {
        val pinPreferences = FakePinPreferences("1234")
        val viewModel = LoginViewModel(pinPreferences)

        repeat(4) { viewModel.onDigitClicked(9) }
        viewModel.onPrimaryActionClicked()

        assertFalse(viewModel.uiState.value.isCompleted)
        assertEquals(LoginError.IncorrectPin, viewModel.uiState.value.error)
        assertEquals("", viewModel.uiState.value.pin)
    }
}
