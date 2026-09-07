package com.afoxplus.emergency.presentation.home

import com.afoxplus.emergency.domain.repository.QuickAlertManager

class FakeQuickAlertManager(
    private var isEnabled: Boolean = false
) : QuickAlertManager {

    var enableQuickAlertCalled = false
        private set

    var disableQuickAlertCalled = false
        private set

    override fun setQuickAlertEnabled(enabled: Boolean) {
        this.isEnabled = enabled
        if (enabled) {
            enableQuickAlert()
        } else {
            disableQuickAlert()
        }
    }

    override fun isQuickAlertEnabled(): Boolean = isEnabled

    override fun enableQuickAlert() {
        isEnabled = true
        enableQuickAlertCalled = true
    }

    override fun disableQuickAlert() {
        isEnabled = false
        disableQuickAlertCalled = true
    }
}
