package com.afoxplus.emergency.presentation.features.history

import com.afoxplus.emergency.domain.model.AlertHistoryEntry
import com.afoxplus.emergency.domain.model.AlertType

/**
 * Filter tabs displayed on the Alert History screen. [ALL] ("Todos") shows every alert,
 * while the rest filter the list down to a single [AlertType].
 */
enum class HistoryFilter {
    ALL,
    SOS_BUTTON,
    QUICK_ALERT,
    PERIODIC_CHECK;

    fun matches(entry: AlertHistoryEntry): Boolean = when (this) {
        ALL -> true
        SOS_BUTTON -> entry.type == AlertType.SOS_BUTTON
        QUICK_ALERT -> entry.type == AlertType.QUICK_ALERT
        PERIODIC_CHECK -> entry.type == AlertType.PERIODIC_CHECK
    }
}

/**
 * Immutable UI state for the Alert History screen: every persisted [AlertHistoryEntry]
 * (most recent first) plus the currently [selectedFilter].
 */
data class HistoryUiState(
    val entries: List<AlertHistoryEntry> = emptyList(),
    val selectedFilter: HistoryFilter = HistoryFilter.ALL
) {
    /** Entries matching the [selectedFilter], most recent first. */
    val filteredEntries: List<AlertHistoryEntry>
        get() = entries.filter { selectedFilter.matches(it) }

    /** Whether the empty state should be shown, e.g. no alerts yet or none match the filter. */
    val isEmpty: Boolean
        get() = filteredEntries.isEmpty()
}
