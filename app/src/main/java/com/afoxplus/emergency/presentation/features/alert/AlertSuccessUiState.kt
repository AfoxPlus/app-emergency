package com.afoxplus.emergency.presentation.features.alert

import com.afoxplus.emergency.domain.model.EmergencyContact

data class AlertSuccessUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val sosMessage: String = ""
) {
    val hasContacts: Boolean
        get() = contacts.isNotEmpty()
}
