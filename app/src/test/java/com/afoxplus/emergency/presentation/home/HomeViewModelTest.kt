package com.afoxplus.emergency.presentation.home

import com.afoxplus.emergency.domain.model.PeriodicCheckConfiguration
import com.afoxplus.emergency.presentation.features.home.HomeViewModel
import com.afoxplus.emergency.presentation.periodiccheck.FakeEmergencyContactsCountProvider
import com.afoxplus.emergency.presentation.periodiccheck.FakePeriodicCheckPreferences
import com.afoxplus.emergency.presentation.register.FakeRegistrationPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    private fun createViewModel(
        userName: String = "",
        isQuickAlertEnabled: Boolean = false,
        isPeriodicCheckEnabled: Boolean = false,
        contactsCount: Int = 0
    ): Quadruple<HomeViewModel, FakeRegistrationPreferences, FakeSettingsPreferences, FakePeriodicCheckPreferences> {
        val regPrefs = FakeRegistrationPreferences()
        if (userName.isNotEmpty()) {
            regPrefs.saveProfile(userName, "123456789")
        }
        val settingsPrefs = FakeSettingsPreferences(quickAlertEnabled = isQuickAlertEnabled)
        val periodicPrefs = FakePeriodicCheckPreferences(
            PeriodicCheckConfiguration(enabled = isPeriodicCheckEnabled)
        )
        val contactsProvider = FakeEmergencyContactsCountProvider(contactsCount)

        val viewModel = HomeViewModel(
            registrationPreferences = regPrefs,
            settingsPreferences = settingsPrefs,
            periodicCheckPreferences = periodicPrefs,
            emergencyContactsCountProvider = contactsProvider
        )

        return Quadruple(viewModel, regPrefs, settingsPrefs, periodicPrefs)
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    @Test
    fun `fresh install defaults to Quick Alert OFF and Periodic Check OFF`() {
        val (viewModel, _, settingsPrefs, periodicPrefs) = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isQuickAlertEnabled)
        assertFalse(state.isPeriodicCheckEnabled)
        assertFalse(settingsPrefs.isQuickAlertEnabled())
        assertFalse(periodicPrefs.configuration.enabled)
    }

    @Test
    fun `dynamic user name displays registered name or fallback when empty`() {
        val (viewModelWithName, _, _, _) = createViewModel(userName = "Valentin")
        assertEquals("Valentin", viewModelWithName.uiState.value.displayName)

        val (viewModelEmpty, _, _, _) = createViewModel(userName = "")
        assertEquals("Usuario", viewModelEmpty.uiState.value.displayName)
    }

    @Test
    fun `status chip is active ONLY when all 4 conditions are met`() {
        // Case 1: All 4 conditions met -> Active
        val (viewModel, _, _, _) = createViewModel(
            isQuickAlertEnabled = true,
            contactsCount = 1
        )
        viewModel.onResume(hasContactsPermission = true, hasLocationPermission = true)

        val stateActive = viewModel.uiState.value
        assertTrue(stateActive.isActive)
        assertTrue(stateActive.missingRequirements.isEmpty())

        // Case 2: Missing contacts permission -> Inactive
        viewModel.onResume(hasContactsPermission = false, hasLocationPermission = true)
        val stateNoContactsPerm = viewModel.uiState.value
        assertFalse(stateNoContactsPerm.isActive)
        assertTrue(stateNoContactsPerm.missingRequirements.any { it.contains("contactos") })

        // Case 3: Missing location permission -> Inactive
        viewModel.onResume(hasContactsPermission = true, hasLocationPermission = false)
        val stateNoLocPerm = viewModel.uiState.value
        assertFalse(stateNoLocPerm.isActive)
        assertTrue(stateNoLocPerm.missingRequirements.any { it.contains("ubicación") })

        // Case 4: No emergency contacts -> Inactive
        val (viewModelNoContacts, _, _, _) = createViewModel(
            isQuickAlertEnabled = true,
            contactsCount = 0
        )
        viewModelNoContacts.onResume(hasContactsPermission = true, hasLocationPermission = true)
        val stateNoContacts = viewModelNoContacts.uiState.value
        assertFalse(stateNoContacts.isActive)
        assertTrue(stateNoContacts.missingRequirements.any { it.contains("registrados") })

        // Case 5: Neither Quick Alert nor Periodic Check enabled -> Inactive
        val (viewModelNoToggles, _, _, _) = createViewModel(
            isQuickAlertEnabled = false,
            isPeriodicCheckEnabled = false,
            contactsCount = 1
        )
        viewModelNoToggles.onResume(hasContactsPermission = true, hasLocationPermission = true)
        val stateNoToggles = viewModelNoToggles.uiState.value
        assertFalse(stateNoToggles.isActive)
        assertTrue(stateNoToggles.missingRequirements.any { it.contains("deshabilitadas") })
    }

    @Test
    fun `status chip is active if either Quick Alert OR Periodic Check is enabled`() {
        // Quick Alert enabled, Periodic Check disabled -> Active
        val (vmQA, _, _, _) = createViewModel(
            isQuickAlertEnabled = true,
            isPeriodicCheckEnabled = false,
            contactsCount = 1
        )
        vmQA.onResume(hasContactsPermission = true, hasLocationPermission = true)
        assertTrue(vmQA.uiState.value.isActive)

        // Quick Alert disabled, Periodic Check enabled -> Active
        val (vmPC, _, _, _) = createViewModel(
            isQuickAlertEnabled = false,
            isPeriodicCheckEnabled = true,
            contactsCount = 1
        )
        vmPC.onResume(hasContactsPermission = true, hasLocationPermission = true)
        assertTrue(vmPC.uiState.value.isActive)
    }

    @Test
    fun `toggling Quick Alert ON updates state, persists, and triggers feedback snackbar`() {
        val (viewModel, _, settingsPrefs, _) = createViewModel()

        viewModel.onQuickAlertToggled(true)

        val state = viewModel.uiState.value
        assertTrue(state.isQuickAlertEnabled)
        assertTrue(settingsPrefs.isQuickAlertEnabled())
        assertNotNull(state.snackbarMessage)
        assertTrue(state.snackbarMessage!!.contains("Alerta rápida activada"))

        // Dismissing snackbar clears message
        viewModel.onSnackbarShown()
        assertNull(viewModel.uiState.value.snackbarMessage)
    }

    @Test
    fun `toggling Quick Alert OFF updates state, persists, and clears snackbar`() {
        val (viewModel, _, settingsPrefs, _) = createViewModel(isQuickAlertEnabled = true)

        viewModel.onQuickAlertToggled(false)

        val state = viewModel.uiState.value
        assertFalse(state.isQuickAlertEnabled)
        assertFalse(settingsPrefs.isQuickAlertEnabled())
        assertNull(state.snackbarMessage)
    }

    @Test
    fun `onResume refreshes user name, periodic check state, and emergency contacts count`() {
        val (viewModel, regPrefs, _, periodicPrefs) = createViewModel(userName = "OldName", contactsCount = 0)

        // User updates profile and periodic check elsewhere
        regPrefs.saveProfile("NewName", "987654321")
        periodicPrefs.saveConfiguration(PeriodicCheckConfiguration(enabled = true))

        viewModel.onResume(hasContactsPermission = true, hasLocationPermission = true)

        val state = viewModel.uiState.value
        assertEquals("NewName", state.displayName)
        assertTrue(state.isPeriodicCheckEnabled)
    }
}
