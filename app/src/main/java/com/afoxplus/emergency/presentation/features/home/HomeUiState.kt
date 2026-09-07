package com.afoxplus.emergency.presentation.features.home

data class HomeUiState(
    val userName: String = "",
    val isQuickAlertEnabled: Boolean = false,
    val isPeriodicCheckEnabled: Boolean = false,
    val hasContactsPermission: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val emergencyContactsCount: Int = 0,
    val snackbarMessage: String? = null
) {
    val displayName: String
        get() = userName.ifBlank { "Usuario" }

    val hasEmergencyContacts: Boolean
        get() = emergencyContactsCount > 0

    val isActive: Boolean
        get() = hasContactsPermission &&
            hasLocationPermission &&
            hasEmergencyContacts &&
            (isQuickAlertEnabled || isPeriodicCheckEnabled)

    val missingRequirements: List<String>
        get() {
            val list = mutableListOf<String>()
            if (!hasContactsPermission) {
                list.add("Permiso de lectura de contactos no concedido")
            }
            if (!hasLocationPermission) {
                list.add("Permiso de ubicación precisa no concedido")
            }
            if (!hasEmergencyContacts) {
                list.add("Sin contactos de emergencia registrados")
            }
            if (!isQuickAlertEnabled && !isPeriodicCheckEnabled) {
                list.add("Alerta rápida y Comprobación periódica deshabilitadas")
            }
            return list
        }
}
