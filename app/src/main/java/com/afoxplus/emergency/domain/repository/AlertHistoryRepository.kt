package com.afoxplus.emergency.domain.repository

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertStatus

/**
 * Persistence contract for the Alert History: every triggered alert (SOS Button, Quick Alert
 * or Periodic Check-in) is recorded here so it can be reviewed later on the History screen.
 */
interface AlertHistoryRepository {
    /** All persisted alerts, most recent first. */
    fun getHistory(): List<AlertHistoryEntry>

    /** Persists a newly triggered alert. */
    fun addEntry(entry: AlertHistoryEntry)

    /** Updates the status of a previously persisted alert, e.g. when it is cancelled. */
    fun updateStatus(id: String, status: AlertStatus)
}
