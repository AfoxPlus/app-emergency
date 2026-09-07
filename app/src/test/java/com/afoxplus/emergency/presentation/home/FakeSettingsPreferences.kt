package com.afoxplus.emergency.presentation.home

import com.afoxplus.emergency.domain.repository.SettingsPreferences

class FakeSettingsPreferences(
    private var sosMessage: String = "¡Emergencia! Necesito ayuda inmediata.",
    private var quickAlertEnabled: Boolean = false
) : SettingsPreferences {

    override fun getSosMessage(): String = sosMessage

    override fun saveSosMessage(message: String) {
        this.sosMessage = message
    }

    override fun isQuickAlertEnabled(): Boolean = quickAlertEnabled

    override fun setQuickAlertEnabled(enabled: Boolean) {
        this.quickAlertEnabled = enabled
    }
}
