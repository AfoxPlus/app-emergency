package com.afoxplus.emergency.presentation.history

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.domain.usecase.FakeAlertHistoryRepository
import com.afoxplus.emergency.presentation.features.history.HistoryFilter
import com.afoxplus.emergency.presentation.features.history.HistoryViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryViewModelTest {

    private val sosEntry = AlertHistoryEntry(
        id = "1",
        type = AlertType.SOS_BUTTON,
        status = AlertStatus.ISSUED,
        timestampMillis = 2_000L,
        description = "SOS"
    )
    private val quickEntry = AlertHistoryEntry(
        id = "2",
        type = AlertType.QUICK_ALERT,
        status = AlertStatus.CANCELLED,
        timestampMillis = 1_000L,
        description = "Quick"
    )

    @Test
    fun `loads every persisted alert on creation`() {
        val repository = FakeAlertHistoryRepository().apply {
            addEntry(sosEntry)
            addEntry(quickEntry)
        }

        val viewModel = HistoryViewModel(repository)

        assertEquals(2, viewModel.uiState.value.entries.size)
        assertEquals(HistoryFilter.ALL, viewModel.uiState.value.selectedFilter)
    }

    @Test
    fun `onFilterSelected updates the selected filter and filters the visible entries`() {
        val repository = FakeAlertHistoryRepository().apply {
            addEntry(sosEntry)
            addEntry(quickEntry)
        }
        val viewModel = HistoryViewModel(repository)

        viewModel.onFilterSelected(HistoryFilter.SOS_BUTTON)

        assertEquals(HistoryFilter.SOS_BUTTON, viewModel.uiState.value.selectedFilter)
        assertEquals(listOf(sosEntry), viewModel.uiState.value.filteredEntries)
    }

    @Test
    fun `onRefreshRequested reloads the entries from the repository`() {
        val repository = FakeAlertHistoryRepository()
        val viewModel = HistoryViewModel(repository)
        assertEquals(0, viewModel.uiState.value.entries.size)

        repository.addEntry(sosEntry)
        viewModel.onRefreshRequested()

        assertEquals(1, viewModel.uiState.value.entries.size)
    }
}
