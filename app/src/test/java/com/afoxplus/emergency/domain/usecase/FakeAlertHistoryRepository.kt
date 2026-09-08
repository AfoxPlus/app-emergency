package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.repository.AlertHistoryRepository

/**
 * In-memory [AlertHistoryRepository] used in tests.
 */
class FakeAlertHistoryRepository : AlertHistoryRepository {

    private val entries = mutableListOf<AlertHistoryEntry>()

    override fun getHistory(): List<AlertHistoryEntry> =
        entries.sortedByDescending { it.timestampMillis }

    override fun addEntry(entry: AlertHistoryEntry) {
        entries += entry
    }

    override fun updateStatus(id: String, status: AlertStatus) {
        val index = entries.indexOfFirst { it.id == id }
        if (index != -1) {
            entries[index] = entries[index].copy(status = status)
        }
    }
}
