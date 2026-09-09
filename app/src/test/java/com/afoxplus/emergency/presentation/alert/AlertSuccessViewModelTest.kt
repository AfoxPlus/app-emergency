package com.afoxplus.emergency.presentation.alert

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.model.Contact
import com.afoxplus.emergency.domain.usecase.FakeAlertHistoryRepository
import com.afoxplus.emergency.presentation.contacts.FakeEmergencyContactRepository
import com.afoxplus.emergency.presentation.features.alert.AlertSuccessViewModel
import com.afoxplus.emergency.presentation.home.FakeSettingsPreferences
import org.junit.Assert.assertEquals
import org.junit.Test

class AlertSuccessViewModelTest {

    @Test
    fun `onCancelAlert marks the matching history entry as cancelled`() {
        val repository = FakeAlertHistoryRepository().apply {
            addEntry(
                AlertHistoryEntry(
                    id = "1",
                    type = AlertType.SOS_BUTTON,
                    status = AlertStatus.ISSUED,
                    timestampMillis = 1_000L,
                    description = "SOS"
                )
            )
        }
        val viewModel = AlertSuccessViewModel(
            alertHistoryRepository = repository,
            emergencyContactRepository = FakeEmergencyContactRepository(),
            settingsPreferences = FakeSettingsPreferences()
        )

        viewModel.onCancelAlert("1")

        assertEquals(AlertStatus.CANCELLED, repository.getHistory().single().status)
    }

    @Test
    fun `onCancelAlert does nothing when the history entry id is null`() {
        val repository = FakeAlertHistoryRepository().apply {
            addEntry(
                AlertHistoryEntry(
                    id = "1",
                    type = AlertType.SOS_BUTTON,
                    status = AlertStatus.ISSUED,
                    timestampMillis = 1_000L,
                    description = "SOS"
                )
            )
        }
        val viewModel = AlertSuccessViewModel(
            alertHistoryRepository = repository,
            emergencyContactRepository = FakeEmergencyContactRepository(),
            settingsPreferences = FakeSettingsPreferences()
        )

        viewModel.onCancelAlert(null)

        assertEquals(AlertStatus.ISSUED, repository.getHistory().single().status)
    }

    @Test
    fun `ui state loads emergency contacts and sos message on init`() {
        val repository = FakeAlertHistoryRepository()
        val emergencyContacts = FakeEmergencyContactRepository(
            initialContacts = listOf(
                Contact(id = "1", name = "Mamá", phoneNumber = "987654321"),
                Contact(id = "2", name = "Carlos", phoneNumber = "912345678")
            )
        )
        val settingsPreferences = FakeSettingsPreferences(sosMessage = "Ayuda por favor")

        val viewModel = AlertSuccessViewModel(
            alertHistoryRepository = repository,
            emergencyContactRepository = emergencyContacts,
            settingsPreferences = settingsPreferences
        )

        assertEquals(emergencyContacts.getEmergencyContacts(), viewModel.uiState.value.contacts)
        assertEquals("Ayuda por favor", viewModel.uiState.value.sosMessage)
    }
}
