package com.afoxplus.emergency.presentation.features.alert

import androidx.lifecycle.ViewModel
import com.afoxplus.emergency.domain.model.AlertStatus
import com.afoxplus.emergency.domain.repository.AlertHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Owns the Alert Activated screen's only piece of business logic: marking the corresponding
 * Alert History entry as cancelled when the user taps "Cancelar alerta" before completion.
 */
@HiltViewModel
class AlertSuccessViewModel @Inject constructor(
    private val alertHistoryRepository: AlertHistoryRepository
) : ViewModel() {

    fun onCancelAlert(historyEntryId: String?) {
        if (historyEntryId != null) {
            alertHistoryRepository.updateStatus(historyEntryId, AlertStatus.CANCELLED)
        }
    }
}
