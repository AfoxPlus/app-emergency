package com.afoxplus.emergency.domain.repository

/**
 * Contract for managing the Periodic Check background service lifecycle.
 */
interface PeriodicCheckManager {
    fun setPeriodicCheckEnabled(enabled: Boolean)
    fun enablePeriodicCheck()
    fun disablePeriodicCheck()
}
