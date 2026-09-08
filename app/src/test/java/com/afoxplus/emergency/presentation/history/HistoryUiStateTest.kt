package com.afoxplus.emergency.presentation.history

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.model.AlertType
import com.afoxplus.emergency.presentation.features.history.HistoryFilter
import com.afoxplus.emergency.presentation.features.history.HistoryUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryUiStateTest {

    private val sosEntry = AlertHistoryEntry(
        id = "1",
        type = AlertType.SOS_BUTTON,
        status = AlertStatus.ISSUED,
        timestampMillis = 3_000L,
        description = "SOS"
    )
    private val quickEntry = AlertHistoryEntry(
        id = "2",
        type = AlertType.QUICK_ALERT,
        status = AlertStatus.CANCELLED,
        timestampMillis = 2_000L,
        description = "Quick"
    )
    private val periodicEntry = AlertHistoryEntry(
        id = "3",
        type = AlertType.PERIODIC_CHECK,
        status = AlertStatus.ISSUED,
        timestampMillis = 1_000L,
        description = "Periodic"
    )

    @Test
    fun `ALL filter shows every entry`() {
        val state = HistoryUiState(entries = listOf(sosEntry, quickEntry, periodicEntry))

        assertEquals(3, state.filteredEntries.size)
        assertFalse(state.isEmpty)
    }

    @Test
    fun `selecting a type filter only shows matching entries`() {
        val state = HistoryUiState(
            entries = listOf(sosEntry, quickEntry, periodicEntry),
            selectedFilter = HistoryFilter.SOS_BUTTON
        )

        assertEquals(listOf(sosEntry), state.filteredEntries)
    }

    @Test
    fun `isEmpty is true when no entries match the selected filter`() {
        val state = HistoryUiState(
            entries = listOf(quickEntry, periodicEntry),
            selectedFilter = HistoryFilter.SOS_BUTTON
        )

        assertTrue(state.isEmpty)
    }

    @Test
    fun `isEmpty is true when there are no entries at all`() {
        val state = HistoryUiState()

        assertTrue(state.isEmpty)
    }
}
