package com.afoxplus.emergency.presentation.alert

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.usecase.FakeAlertHistoryRepository
import com.afoxplus.emergency.presentation.features.alert.AlertSuccessViewModel
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
        val viewModel = AlertSuccessViewModel(repository)

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
        val viewModel = AlertSuccessViewModel(repository)

        viewModel.onCancelAlert(null)

        assertEquals(AlertStatus.ISSUED, repository.getHistory().single().status)
    }
}
