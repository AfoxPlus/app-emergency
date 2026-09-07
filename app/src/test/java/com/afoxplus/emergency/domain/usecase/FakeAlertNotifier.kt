package com.afoxplus.emergency.domain.usecase

import com.afoxplus.emergency.domain.repository.AlertNotifier

class FakeAlertNotifier : AlertNotifier {

    var alertSentNotifiedWith: List<String>? = null
        private set

    var alertFailedNotifiedWith: String? = null
        private set

    var noContactsNotified: Boolean = false
        private set

    override fun notifyAlertSent(contactNames: List<String>) {
        alertSentNotifiedWith = contactNames
    }

    override fun notifyAlertFailed(errorMessage: String) {
        alertFailedNotifiedWith = errorMessage
    }

    override fun notifyNoContactsConfigured() {
        noContactsNotified = true
    }
}
