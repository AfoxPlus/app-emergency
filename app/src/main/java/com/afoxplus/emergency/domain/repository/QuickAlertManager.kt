package com.afoxplus.emergency.domain.repository

/**
 * Contract for managing the Quick Alert background monitoring service.
 */
interface QuickAlertManager {
    fun setQuickAlertEnabled(enabled: Boolean)
    fun isQuickAlertEnabled(): Boolean
    fun enableQuickAlert()
    fun disableQuickAlert()
}
