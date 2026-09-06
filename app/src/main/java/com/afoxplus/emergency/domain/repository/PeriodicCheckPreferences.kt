package com.afoxplus.emergency.domain.repository

import com.afoxplus.emergency.domain.model.PeriodicCheckConfiguration

/**
 * Persistence contract for the Periodic Safety Check configuration.
 */
interface PeriodicCheckPreferences {
    fun getConfiguration(): PeriodicCheckConfiguration
    fun saveConfiguration(configuration: PeriodicCheckConfiguration)
}
