package com.afoxplus.emergency.presentation.alert

import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.presentation.contacts.FakeEmergencyContactRepository
import com.afoxplus.emergency.presentation.features.alert.AlertSuccessViewModel
import com.afoxplus.emergency.presentation.home.FakeSettingsPreferences
import org.junit.Assert.assertEquals
import org.junit.Test

class AlertSuccessViewModelTest {

    @Test
    fun `ui state loads emergency contacts and sos message on init`() {
        val emergencyContacts = FakeEmergencyContactRepository(
            initialContacts = listOf(
                Contact(id = "1", name = "Mamá", phoneNumber = "987654321"),
                Contact(id = "2", name = "Carlos", phoneNumber = "912345678")
            )
        )
        val settingsPreferences = FakeSettingsPreferences(sosMessage = "Ayuda por favor")
        val viewModel = AlertSuccessViewModel(
            emergencyContactRepository = emergencyContacts,
            settingsPreferences = settingsPreferences
        )

        assertEquals(emergencyContacts.getEmergencyContacts(), viewModel.uiState.value.contacts)
        assertEquals("Ayuda por favor", viewModel.uiState.value.sosMessage)
    }
}
