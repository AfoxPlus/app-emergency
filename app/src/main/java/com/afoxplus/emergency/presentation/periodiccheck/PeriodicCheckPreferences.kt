package com.afoxplus.emergency.presentation.periodiccheck

import android.content.Context

/**
 * Persistence contract for the Periodic Safety Check configuration (AC18/AC19).
 *
 * Kept as an interface so [PeriodicCheckViewModel] can be unit tested without any
 * Android framework dependency, following the app's separation between UI state and
 * business/persistence logic.
 */
interface PeriodicCheckPreferences {
    fun getConfiguration(): PeriodicCheckConfiguration
    fun saveConfiguration(configuration: PeriodicCheckConfiguration)
}

/**
 * [PeriodicCheckPreferences] implementation backed by [android.content.SharedPreferences],
 * consistent with the persistence approach used by other features in this app
 * (see `OnboardingPreferences`, `RegistrationPreferences`).
 */
class PeriodicCheckPreferencesImpl(context: Context) : PeriodicCheckPreferences {

    private val sharedPreferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getConfiguration(): PeriodicCheckConfiguration = PeriodicCheckConfiguration(
        enabled = sharedPreferences.getBoolean(KEY_ENABLED, false),
        frequencyMinutes = sharedPreferences.getInt(
            KEY_FREQUENCY_MINUTES,
            PeriodicCheckConfiguration.DEFAULT_FREQUENCY_MINUTES
        ),
        responseTimeoutMinutes = sharedPreferences.getInt(
            KEY_RESPONSE_TIMEOUT_MINUTES,
            PeriodicCheckConfiguration.DEFAULT_RESPONSE_TIMEOUT_MINUTES
        ),
        lastConfigurationUpdate = sharedPreferences.getLong(KEY_LAST_UPDATE, 0L)
    )

    override fun saveConfiguration(configuration: PeriodicCheckConfiguration) {
        sharedPreferences.edit()
            .putBoolean(KEY_ENABLED, configuration.enabled)
            .putInt(KEY_FREQUENCY_MINUTES, configuration.frequencyMinutes)
            .putInt(KEY_RESPONSE_TIMEOUT_MINUTES, configuration.responseTimeoutMinutes)
            .putLong(KEY_LAST_UPDATE, configuration.lastConfigurationUpdate)
            .apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "periodic_check_preferences"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_FREQUENCY_MINUTES = "frequency_minutes"
        private const val KEY_RESPONSE_TIMEOUT_MINUTES = "response_timeout_minutes"
        private const val KEY_LAST_UPDATE = "last_configuration_update"
    }
}
