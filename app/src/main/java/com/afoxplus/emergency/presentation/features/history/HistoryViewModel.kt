package com.afoxplus.emergency.presentation.features.history

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.repository.AlertHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Owns the Alert History UI state: loads every persisted alert from [AlertHistoryRepository]
 * and applies the selected [HistoryFilter] (AC03/AC04).
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val alertHistoryRepository: AlertHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(entries = alertHistoryRepository.getHistory()))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    /** AC04: selecting a filter tab updates the visible list. */
    fun onFilterSelected(filter: HistoryFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    /** Refreshes the list, e.g. when the screen becomes visible again after a new alert. */
    fun onRefreshRequested() {
        _uiState.update { it.copy(entries = alertHistoryRepository.getHistory()) }
    }
}
