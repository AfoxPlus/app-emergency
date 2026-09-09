package com.afoxplus.emergency.presentation.periodiccheck

import com.afoxplus.emergency.domain.repository.PeriodicCheckManager

class FakePeriodicCheckManager : PeriodicCheckManager {
    var enabled: Boolean = false
        private set

    var enablePeriodicCheckCalled: Boolean = false
        private set

    var disablePeriodicCheckCalled: Boolean = false
        private set

    override fun setPeriodicCheckEnabled(enabled: Boolean) {
        this.enabled = enabled
        if (enabled) {
            enablePeriodicCheck()
        } else {
            disablePeriodicCheck()
        }
    }

    override fun enablePeriodicCheck() {
        enabled = true
        enablePeriodicCheckCalled = true
    }

    override fun disablePeriodicCheck() {
        enabled = false
        disablePeriodicCheckCalled = true
    }
}
